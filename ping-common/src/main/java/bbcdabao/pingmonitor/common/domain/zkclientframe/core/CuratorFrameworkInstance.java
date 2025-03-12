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

package bbcdabao.pingmonitor.common.domain.zkclientframe.core;

import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

import bbcdabao.pingmonitor.common.domain.FactoryBase;
import bbcdabao.pingmonitor.common.domain.zkclientframe.ZkclientframeConfig;


/**
 * CuratorFramework Manager
 */
public class CuratorFrameworkInstance {

    private static class Holder {
        private static final CuratorFramework INSTANCE = createCuratorFramework();
    }

    private CuratorFrameworkInstance() {
    }

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
        ZkclientframeConfig zkclientframeConfig = FactoryBase
                .getFactory().getBean(ZkclientframeConfig.class);
        if (null == zkclientframeConfig) {
            System.exit(1);
        }

        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .retryPolicy(retryPolicy)
                .namespace(zkclientframeConfig.getNameSpace())
                .connectString(zkclientframeConfig.getConnectString())
                .sessionTimeoutMs(zkclientframeConfig.getSessionTimeoutMs())
                .connectionTimeoutMs(zkclientframeConfig.getConnectionTimeoutMs())
                .build();

        curatorFramework.start();
        registerShutdownHook();
        return curatorFramework;
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close();
        }));
    }

    private static synchronized void close() {
        Holder.INSTANCE.close();
        System.out.println("CuratorFramework closed.");
    }

    public static CuratorFramework getInstance() {
        return Holder.INSTANCE;
    }
}
