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

package bbcdabao.pingmonitor.common.domain;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingmonitorExecutor {

    private static class Holder {
        private static final ExecutorService INSTANCE = getExecutorService();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PingmonitorExecutor.class);

    private static ExecutorService getExecutorService() {
        PingmonitorExecutorConfig config = FactoryBase
                .getFactory().getBean(PingmonitorExecutorConfig.class);
        ExecutorService executor = new ThreadPoolExecutor(
                config.getCorePoolSize(),
                config.getMaxPoolSize(),
                config.getKeepAliveSeconds(),
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(config.getQueueCapacity()), new ThreadFactory() {
                    private AtomicInteger count = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, config.getThreadNamePrefix() + "-" + count.getAndIncrement());
                    }
                }, new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        LOGGER.info("{}: RejectedExecutionHandler", config.getThreadNamePrefix());
                    }
                });
        return executor;
    }

    public static ExecutorService getInstance() {
        return Holder.INSTANCE;
    }
}
