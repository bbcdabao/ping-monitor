/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package bbcdabao.pingmonitor.common.configs;

import java.io.Closeable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Thread pool config.
 */
public class ThreadPoolConfig {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolConfig.class);

    private Integer corePoolSize;

    private Integer maxPoolSize;

    private Integer queueCapacity;

    private Integer keepAliveSeconds;

    private String threadNamePrefix;

    /**
     * Thread pool
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "ping-monitor.thread-pool", name= "enable", havingValue = "true", matchIfMissing = false)
    public ExecutorService pingMonitorThreadPoolExecutor() {
        ExecutorService executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadFactory() {
                    private int count = 0;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, threadNamePrefix + "-" + count);
                        count++;
                        return thread;
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        if (r instanceof Closeable) {
                            Closeable rClost = (Closeable)r;
                            try {
                                rClost.close();
                            } catch (Exception e) {
                                logger.info("wscThreadPoolExecutor RejectedExecutionHandler error:{}", e.getMessage());
                            }
                        }
                    }
                }
            );
        return executor;
    }
}
