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

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import bbcdabao.pingmonitor.common.infra.domainconfig.SpringContextHolder;
import bbcdabao.pingmonitor.common.infra.domainconfig.configs.PingmonitorExecutorConfig;

public class PingmonitorExecutor {

    private static class Holder {
        private static final ThreadPoolTaskExecutor INSTANCE = getThreadPoolTaskExecutor();
    }

    private static ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        PingmonitorExecutorConfig config = SpringContextHolder.getBean(PingmonitorExecutorConfig.class);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getCorePoolSize());
        executor.setMaxPoolSize(config.getMaxPoolSize());
        executor.setQueueCapacity(config.getQueueCapacity());
        executor.setKeepAliveSeconds(config.getKeepAliveSeconds());
        executor.setThreadNamePrefix(config.getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    public static ThreadPoolTaskExecutor getInstance() {
        return Holder.INSTANCE;
    }
}
