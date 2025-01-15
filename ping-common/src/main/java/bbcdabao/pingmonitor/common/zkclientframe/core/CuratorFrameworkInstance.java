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

    private static volatile Config CONFIG_PARAM = null;

    public static void setConfig(Config config) {
        CONFIG_PARAM = config;
    }

    private static volatile CuratorFramework CURATOR_FRAMEWORK = null;

    private static CuratorFramework createCuratorFramework() {
        if (CONFIG_PARAM == null) {
            throw new IllegalStateException("Config must be set before initializing CuratorFramework.");
        }
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(CONFIG_PARAM.getConnectString(),
                new RetryPolicy() {
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
                });
        curatorFramework.start();
        return curatorFramework;
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close();
        }));
    }

    private static void close() {
        if (CURATOR_FRAMEWORK != null) {
            CURATOR_FRAMEWORK.close();
            System.out.println("CuratorFramework closed.");
        }
    }

    public static CuratorFramework getInstance() {
        if (CURATOR_FRAMEWORK == null) {
            synchronized (CuratorFrameworkInstance.class) {
                if (CURATOR_FRAMEWORK == null) {
                    CURATOR_FRAMEWORK = createCuratorFramework();
                    registerShutdownHook();
                }
            }
        }
        return CURATOR_FRAMEWORK;
    }
}
