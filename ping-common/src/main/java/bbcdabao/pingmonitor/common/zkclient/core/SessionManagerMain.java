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

package bbcdabao.pingmonitor.common.zkclient.core;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.zkclient.BaseEventHandler;
import bbcdabao.pingmonitor.common.zkclient.IEventHandlerRegister;
import bbcdabao.pingmonitor.common.zkclient.core.PathManager.IEventSender;
import bbcdabao.pingmonitor.common.zkclient.event.ChangedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.CreatedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.DeletedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.IEvent;

/**
 * Handle listening sessions for BaseEventHandler
 */
public class SessionManagerMain implements IEventHandlerRegister {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManagerMain.class);

    private final ExecutorService executorService;

    private final PathManager pathManager;

    private final int qeCapacity;
    private final long scanCycle;

    private SessionManagerMain(
            ExecutorService executorService,
            CuratorFramework client,
            int qeCapacity,
            long scanCycle) {
        this.executorService = executorService;
        this.pathManager = new PathManager(client, scanCycle);
        this.qeCapacity = qeCapacity;
        this.scanCycle = scanCycle;
        executorService.execute(pathManager);
    }

    /**
     * For exit 
     */
    private static class StopException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    private Map<Long, HandlerNode> handlerNodeMap = new HashMap<>(1000);
    private synchronized HandlerNode getHandlerNode(BaseEventHandler handler) {
        long code = handler.getCode();
        HandlerNode handlerNode = handlerNodeMap.computeIfAbsent(code, (k) -> {
            return new HandlerNode(handler);
        });
        return handlerNode;
    }
    private synchronized void delHandlerNode(long code) {
        handlerNodeMap.remove(code);
    }

    /**
     * Driver handles events and manages life cycle
     */
    private class HandlerNode implements Runnable, IEventSender {
        private final WeakReference<BaseEventHandler> weakhandler;
        private final long code;
        private final LinkedBlockingQueue<IEvent> eventsQueue = new LinkedBlockingQueue<>(qeCapacity);

        private HandlerNode(BaseEventHandler handler) {
            this.weakhandler = new WeakReference<>(handler);
            this.code = handler.getCode();
        }

        public void runStep()  throws Exception {
            IEvent event = eventsQueue.poll(scanCycle, TimeUnit.MILLISECONDS);
            BaseEventHandler handler = weakhandler.get();
            if (null == handler) {
                throw new StopException();
            }
            if (event instanceof CreatedEvent) {
                CreatedEvent eventIndex = (CreatedEvent)event;
                handler.onEvent(eventIndex);
            }
            else if (event instanceof ChangedEvent) {
                ChangedEvent eventIndex = (ChangedEvent)event;
                handler.onEvent(eventIndex);
            }
            else if (event instanceof DeletedEvent) {
                DeletedEvent eventIndex = (DeletedEvent)event;
                handler.onEvent(eventIndex);
            }
        }

        @Override
        public void send(IEvent event) {
            if (!eventsQueue.offer(event)) {
                LOGGER.info("offer event fail");
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    runStep();
                } catch (StopException e) {
                    LOGGER.info("session{} stop exit!", code);
                    break;
                } catch (Exception e) {
                    LOGGER.info("session{} Exception:{}", code, e.getMessage());
                }
            }
            delHandlerNode(code);
        } 
    }

    /**
     * To build SessionManagerMain
     * @param path Listen path
     * @param handler Session handler
     */
    public static class Builder {
        /*
         * Thread pool set
         */
        private int corePoolSize = 16;
        private int maxPoolSize = 80;
        private int queueCapacity = 100;
        private int keepAliveSeconds = 60;
        private String threadNamePrefix = "zk-session-thread";
        
        private int qeCapacity = 1000;
        private long scanCycle = 30000;

        private final CuratorFramework zkclient;

        public Builder(CuratorFramework zkclient) {
            this.zkclient = zkclient;
        }

        public Builder setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }
        public Builder setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }
        public Builder setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
            return this;
        }
        public Builder setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
            return this;
        }
        public Builder setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
            return this;
        }
        public IEventHandlerRegister build() {
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
                            LOGGER.info("{}:RejectedExecutionHandler", threadNamePrefix);
                        }
                    }
                );
            return new SessionManagerMain(executor, zkclient, qeCapacity, scanCycle);
        }
    }

    /**
     * For out side call
     * @param path Listen path
     * @param handler Session handler
     */
    @Override
    public void registerEventHandler(String path, BaseEventHandler handler) {
        if (ObjectUtils.isEmpty(path)) {
            return;
        }
        if (null == handler) {
            return;
        }
        HandlerNode handlerNode = getHandlerNode(handler);
        executorService.execute(handlerNode);
        pathManager.addPathListener(path, handlerNode);
    }
}
