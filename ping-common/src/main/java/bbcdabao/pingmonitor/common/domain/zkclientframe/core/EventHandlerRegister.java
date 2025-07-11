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

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.domain.BeanFactoryHolder;
import bbcdabao.pingmonitor.common.domain.PingmonitorExecutor;
import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler.IRegister;
import bbcdabao.pingmonitor.common.domain.zkclientframe.ZkclientframeConfig;
import bbcdabao.pingmonitor.common.domain.zkclientframe.core.PathManager.BaseEventSender;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.IEvent;

/**
 * Handle listening sessions for BaseEventHandler
 */
public class EventHandlerRegister implements IRegister {

    private static class Holder {
        private static final IRegister INSTANCE = getIRegister();
    }

    private static IRegister getIRegister() {
        ZkclientframeConfig config =
                BeanFactoryHolder.getInstance().getBean(ZkclientframeConfig.class);
        return new EventHandlerRegister(PingmonitorExecutor.getInstance(),
                CuratorFrameworkInstance.getInstance(), config.getQeCapacity(), config.getScanCycle());
    }

    public static IRegister getInstance() {
        return Holder.INSTANCE;
    }

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
        HandlerNode handlerNode = handlerNodeMap.get(code);
        if (null != handlerNode) {
            handlerNode.gameOver();
            handlerNodeMap.remove(code);
        }
    }

    /**
     * Driver handles events and manages life cycle
     */
    private class HandlerNode extends BaseEventSender implements Runnable {
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
            if (handler.isOver()) {
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
            } else {
                handler.onIdl();
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

    @Override
    public void regAndBlokingRun(String path, BaseEventHandler handler) {
        if (ObjectUtils.isEmpty(path)) {
            return;
        }
        if (null == handler) {
            return;
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
        pathManager.addPathListener(path, handlerNode);
        handlerNode.run();
    }
}
