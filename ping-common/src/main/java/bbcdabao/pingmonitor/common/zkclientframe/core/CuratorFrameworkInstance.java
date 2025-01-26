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

package bbcdabao.pingmonitor.common.zkclientframe.core;

import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

import lombok.Data;

/**
 * CuratorFramework Manager
 */
public class CuratorFrameworkInstance {

    @Data
    public static class Config {
        private String connectString;
    }

    @FunctionalInterface
    public interface ConfigProvider {
        Config getConfig();
    }

    public static void setConfigProvider(ConfigProvider configProvider) {
        CONFIG_PROVIDER = configProvider;
    }

    private static ConfigProvider CONFIG_PROVIDER = () -> {
        Config config = new Config();
        config.setConnectString("127.0.0.1:2181");
        return config;
    };

    private static volatile CuratorFramework CURATOR_FRAMEWORK = null;

    private static CuratorFramework createCuratorFramework() {
        RetryPolicy retryPolicy = new RetryPolicy() {
            @Override
            public boolean allowRetry(int retryCount, long elapsedTimeMs, RetrySleeper sleeper) {
                try {
                    sleeper.sleepFor(2000, null);
                    return true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        };
        Config config = CONFIG_PROVIDER.getConfig();
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(config.getConnectString(), retryPolicy);
        curatorFramework.start();
        return curatorFramework;
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close();
        }));
    }

    private static synchronized void close() {
        if (CURATOR_FRAMEWORK != null) {
            CURATOR_FRAMEWORK.close();
            CURATOR_FRAMEWORK = null;
            System.out.println("CuratorFramework closed.");
        }
    }

    private static synchronized void initInstance() {
        if (CURATOR_FRAMEWORK == null) {
            CURATOR_FRAMEWORK = createCuratorFramework();
            registerShutdownHook();
        }
    }

    public static CuratorFramework getInstance() {
        if (CURATOR_FRAMEWORK != null) {
            return CURATOR_FRAMEWORK;
        }
        initInstance();
        return CURATOR_FRAMEWORK;
    }
}
