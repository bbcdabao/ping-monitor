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

import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Zookeeper client bean config
 */
@Configuration
public class ZookeeperClientConfig {

    @Value("${ping-monitor.zk-clinet.connect-string:}")
    private String connectString;
    
    @ConditionalOnProperty(prefix = "ping-monitor.zk-clinet", name= "enable", havingValue = "true", matchIfMissing = false)
    public CuratorFramework curatorFramework() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(
            connectString,
            new RetryPolicy() {
                @Override
                public boolean allowRetry(int retryCount, long elapsedTimeMs, RetrySleeper sleeper) {
                    try {
                        sleeper.sleepFor(2000, null);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                    return true;
                }
            });
        curatorFramework.start();
        return curatorFramework;
    }
}
