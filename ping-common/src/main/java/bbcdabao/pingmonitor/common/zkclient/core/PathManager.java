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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.pingmonitor.common.zkclient.event.ChangedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.CreatedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.DeletedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.IEvent;

/**
 * To share monitoring wapper
 */
public class PathManager implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathManager.class);

    private final CuratorFramework client;
    private final long scanCycle;

    /**
     * To share monitoring
     */
    private class PathNode {
        private final LinkedList<WeakReference<IEventSender>> queueEvent = new LinkedList<>();
        private final CuratorCache curatorCache;
        private final String path;
        private PathNode(CuratorCache curatorCache, String path) {
            this.curatorCache = curatorCache;
            this.path = path;
        }
        private void sendEvent(final IEvent event) {
            checks((sender) -> {
                sender.send(event);
            });
        }
        private synchronized void addSender(WeakReference<IEventSender> weakSender) {
            queueEvent.add(weakSender);
        }
        private synchronized boolean checks(Consumer<IEventSender> consumer) {
            Iterator<WeakReference<IEventSender>> iterator = queueEvent.iterator();
            if (null == consumer) {
                while (iterator.hasNext()) {
                    WeakReference<IEventSender> weakSender = iterator.next();
                    IEventSender sender = weakSender.get();
                    if (null == sender) {
                        iterator.remove();
                    }
                }
            } else {
                while (iterator.hasNext()) {
                    WeakReference<IEventSender> weakSender = iterator.next();
                    IEventSender sender = weakSender.get();
                    if (null == sender) {
                        iterator.remove();
                        continue;
                    }
                    if (null == consumer) {
                        continue;
                    }
                    consumer.accept(sender);
                }
            }
            return queueEvent.isEmpty();
        }
    }

    /**
     * Add to send
     */
    private Map<String, PathNode> pathNodeMap = new HashMap<>(1000);
    private synchronized void getPathNode(String path, WeakReference<IEventSender> weakSender) {
        PathNode pathNode = pathNodeMap.computeIfAbsent(path, (k) -> {
            CuratorCache curatorCache = CuratorCache.build(client, path);
            curatorCache.start();
            final PathNode pathNodeNew = new PathNode(curatorCache, path);
            CuratorCacheListener listener  = CuratorCacheListener.builder().forAll((type, oldData, newData) -> {
                IEvent event = null;
                switch (type) {
                case NODE_CREATED:
                    event = new CreatedEvent(newData);
                    break;
                case NODE_CHANGED:
                    event = new ChangedEvent(oldData, newData);
                    break;
                case NODE_DELETED:
                    event = new DeletedEvent(oldData);
                default:
                    return;
                }
                pathNodeNew.sendEvent(event);
            }).build();
            curatorCache.listenable().addListener(listener);
            return pathNodeNew;
        });
        pathNode.addSender(weakSender);
    }

    /**
     * Periodic scheduling of garbage removal
     */
    private synchronized void deleteNodes() {
        Iterator<Map.Entry<String, PathNode>> iterator = pathNodeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PathNode> entry = iterator.next();
            PathNode pathNode = entry.getValue();
            if (pathNode.checks(null)) {
                iterator.remove();
                try (pathNode.curatorCache) {
                    LOGGER.info("PathManager.pathNode:{} delete!", pathNode.path);
                } catch (Exception e) {
                    LOGGER.info("PathManager.pathNode:{} delete! Exception:{}", pathNode.path, e.getMessage());
                }
            }
        }
    }

    /**
     * Sender interface for send zookeeper event
     */
    public static interface IEventSender {
        void send(IEvent event);
    }

    public void addPathListener(String path, IEventSender sender) {
        WeakReference<IEventSender> weakSender = new WeakReference<>(sender);
        getPathNode(path, weakSender);
    }

    public PathManager(CuratorFramework client, long scanCycle) {
        this.client = client;
        this.scanCycle = scanCycle;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(scanCycle);
            } catch(InterruptedException e) {
                LOGGER.info("PathManager.run sleep InterruptedException");
            }
            try {
                deleteNodes();
            } catch (Exception e) {
                LOGGER.info("PathManager.run deleteNodes Exception:{}", e.getMessage());
            }
        }
    }
}
