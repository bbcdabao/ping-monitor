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

package bbcdabao.pingmonitor.common.domain.coordination;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.curator.framework.recipes.leader.LeaderLatch;

import bbcdabao.pingmonitor.common.domain.PingmonitorExecutor;
import bbcdabao.pingmonitor.common.domain.zkclientframe.core.CuratorFrameworkInstance;

/**
 * Master keep run
 */
public class MasterKeeperTaskManager {

    private static class Holder {
        private static final MasterKeeperTaskManager INSTANCE = new MasterKeeperTaskManager();
    }

    public static MasterKeeperTaskManager getInstance() {
        return Holder.INSTANCE;
    }

    private MasterKeeperTaskManager() {
    }

    private void sleepStep() {
        try {
            Thread.sleep(000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void selectMasterRun(String path, Runnable runnable, Consumer<Exception> errorHandler) throws Exception {
        PingmonitorExecutor.getInstance().execute(() -> {
            LeaderLatch leaderLatch = new LeaderLatch(CuratorFrameworkInstance.getInstance(), path);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    leaderLatch.start();
                    break;
                } catch (Exception e) {
                    if (null != errorHandler) {
                        errorHandler.accept(e);
                    } else {
                        e.printStackTrace();
                    }
                    sleepStep();
                }
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (leaderLatch.hasLeadership()) {
                        runnable.run();
                    } else {
                        leaderLatch.await(5, TimeUnit.SECONDS);
                    }
                } catch (Exception e) {
                    if (null != errorHandler) {
                        errorHandler.accept(e);
                    } else {
                        e.printStackTrace();
                    }
                    sleepStep();
                }
            }
            try {
                leaderLatch.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void selectMasterNotify(String path, IMasterNotify set, IMasterNotify cancel, Consumer<Exception> errorHandler) throws Exception {
        PingmonitorExecutor.getInstance().execute(() -> {
            boolean isMaster = false;
            LeaderLatch leaderLatch = new LeaderLatch(CuratorFrameworkInstance.getInstance(), path);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    leaderLatch.start();
                    break;
                } catch (Exception e) {
                    if (null != errorHandler) {
                        errorHandler.accept(e);
                    } else {
                        e.printStackTrace();
                    }
                    sleepStep();
                }
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (leaderLatch.hasLeadership()) {
                        if (!isMaster) {
                            set.notify();
                            isMaster = true; 
                        }
                        Thread.sleep(1000);
                    } else {
                        if (isMaster) {
                            cancel.notify();
                            isMaster = false;
                        }
                        leaderLatch.await(10, TimeUnit.SECONDS);
                    }
                } catch (Exception e) {
                    if (null != errorHandler) {
                        errorHandler.accept(e);
                    } else {
                        e.printStackTrace();
                    }
                    sleepStep();
                }
            }
            try {
                leaderLatch.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
