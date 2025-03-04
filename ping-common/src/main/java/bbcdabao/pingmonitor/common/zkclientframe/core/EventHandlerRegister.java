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

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
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
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.zkclientframe.BaseEventHandler.IRegister;
import bbcdabao.pingmonitor.common.zkclientframe.core.PathManager.IEventSender;
import bbcdabao.pingmonitor.common.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.common.zkclientframe.event.IEvent;

/**
 * Handle listening sessions for BaseEventHandler
 */
public class EventHandlerRegister implements IRegister {

    private static class Holder {
        private static final IRegister INSTANCE = CONFIG_PROVIDER.getDefaultBuilder().build();
    }

    @FunctionalInterface
    public interface ConfigProvider {
        Builder getDefaultBuilder();
    }

    public static void setConfigProvider(ConfigProvider configProvider) {
        CONFIG_PROVIDER = configProvider;
    }

    private static volatile ConfigProvider CONFIG_PROVIDER = () -> {
        return EventHandlerRegister.builder();
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHandlerRegister.class);

    private final ExecutorService executorService;

    private final PathManager pathManager;

    private final int qeCapacity;
    private final long scanCycle;

    private EventHandlerRegister(ExecutorService executorService, CuratorFramework client, int qeCapacity,
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

        public void runStep() throws Exception {
            IEvent event = eventsQueue.poll(scanCycle, TimeUnit.MILLISECONDS);
            BaseEventHandler handler = weakhandler.get();
            if (null == handler) {
                throw new StopException();
            }
            if (event instanceof CreatedEvent) {
                CreatedEvent eventIndex = (CreatedEvent) event;
                handler.onEvent(eventIndex);
            } else if (event instanceof ChangedEvent) {
                ChangedEvent eventIndex = (ChangedEvent) event;
                handler.onEvent(eventIndex);
            } else if (event instanceof DeletedEvent) {
                DeletedEvent eventIndex = (DeletedEvent) event;
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
     * For out side call
     * 
     * @param path    Listen path
     * @param handler Session handler
     */
    @Override
    public CuratorCache reg(String path, BaseEventHandler handler) {
        if (ObjectUtils.isEmpty(path)) {
            return null;
        }
        if (null == handler) {
            return null;
        }
        /**
         * When optimization is needed in the future, add judgment and attention events
         * to monitor.
         */
        try {
            Method methodCreatedEvent = handler.getClass().getMethod("onEvent", CreatedEvent.class);
            Method methodChangedEvent = handler.getClass().getMethod("onEvent", ChangedEvent.class);
            Method methodDeletedEvent = handler.getClass().getMethod("onEvent", DeletedEvent.class);
            if (!methodCreatedEvent.getDeclaringClass().equals(BaseEventHandler.class)) {
                LOGGER.info("methodCreatedEvent is overrwide!");
            }
            if (!methodChangedEvent.getDeclaringClass().equals(BaseEventHandler.class)) {
                LOGGER.info("methodChangedEvent is overrwide!");
            }
            if (!methodDeletedEvent.getDeclaringClass().equals(BaseEventHandler.class)) {
                LOGGER.info("methodDeletedEvent is overrwide!");
            }
        } catch (Exception e) {
        }
        HandlerNode handlerNode = getHandlerNode(handler);
        executorService.execute(handlerNode);
        return pathManager.addPathListener(path, handlerNode);
    }

    /**
     * To build SessionManagerMain
     * 
     * @param path    Listen path
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

        public IRegister build() {
            ExecutorService executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueCapacity), new ThreadFactory() {
                        private int count = 0;

                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r, threadNamePrefix + "-" + count);
                            count++;
                            return thread;
                        }
                    }, new RejectedExecutionHandler() {
                        @Override
                        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                            LOGGER.info("{}: RejectedExecutionHandler", threadNamePrefix);
                        }
                    });
            return new EventHandlerRegister(executor, CuratorFrameworkInstance.getInstance(), qeCapacity, scanCycle);
        }
    }

    /**
     * Return builder
     * 
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Return singnle instance
     * 
     * @return
     */
    public static IRegister getInstance() {
        return Holder.INSTANCE;
    }
}
