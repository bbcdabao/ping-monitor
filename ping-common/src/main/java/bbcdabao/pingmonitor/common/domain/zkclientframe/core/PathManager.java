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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.pingmonitor.common.domain.zkclientframe.GameOver;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.IEvent;

/**
 * To share monitoring wapper, just used by EventHandlerRegister
 */
class PathManager implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathManager.class);

    private final CuratorFramework client;
    private final long scanCycle;

    private static final Map<org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type
    , BiFunction<ChildData, ChildData, IEvent>> EVENT_GETTER = new HashMap<>(3);
    static {
        EVENT_GETTER.put(org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type
                .NODE_CREATED, (oldData, newData) -> {
            return new CreatedEvent(newData);
        });
        EVENT_GETTER.put(org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type
                .NODE_CHANGED, (oldData, newData) -> {
            return new ChangedEvent(oldData, newData);
        });
        EVENT_GETTER.put(org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type
                .NODE_DELETED, (oldData, newData) -> {
            return new DeletedEvent(oldData);
        });
    }

    private static final Map<org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type
    , BiFunction<ChildData, ChildData, IEvent>> CHILD_EVENT_GETTER = new HashMap<>(3);
    static {
        CHILD_EVENT_GETTER.put(org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type
                .CHILD_ADDED, (oldData, newData) -> {
            return new CreatedEvent(newData);
        });
        CHILD_EVENT_GETTER.put(org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type
                .CHILD_UPDATED , (oldData, newData) -> {
            return new ChangedEvent(oldData, newData);
        });
        CHILD_EVENT_GETTER.put(org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type
                .CHILD_REMOVED, (oldData, newData) -> {
            return new DeletedEvent(oldData);
        });
    }

    private class NodeQueue {
        private final LinkedList<WeakReference<BaseEventSender>> queueEvent =
                new LinkedList<>();
        private synchronized void addSender(WeakReference<BaseEventSender> weakSender) {
            queueEvent.add(weakSender);
        }
        private synchronized boolean checks(Consumer<BaseEventSender> consumer) {
            Iterator<WeakReference<BaseEventSender>> iterator = queueEvent.iterator();
            if (null == consumer) {
                while (iterator.hasNext()) {
                    WeakReference<BaseEventSender> weakSender = iterator.next();
                    BaseEventSender sender = weakSender.get();
                    if (null == sender) {
                        iterator.remove();
                    } else if (sender.isOver()) {
                        iterator.remove();
                    }
                }
            } else {
                while (iterator.hasNext()) {
                    WeakReference<BaseEventSender> weakSender = iterator.next();
                    BaseEventSender sender = weakSender.get();
                    if (null == sender) {
                        iterator.remove();
                        continue;
                    } else if (sender.isOver()) {
                        iterator.remove();
                        continue;
                    }
                    consumer.accept(sender);
                }
            }
            return queueEvent.isEmpty();
        }
    }
    
    private class NodeInfo {
        private final CuratorCacheListener listener;
        private final NodeQueue nodeQueue;
        private NodeInfo(CuratorCacheListener listener, NodeQueue nodeQueue) {
            this.listener = listener;
            this.nodeQueue = nodeQueue;
        }
    }

    /**
     * To share monitoring
     */
    private class PathNode {
        private final Map<String, NodeInfo> queueMap = new HashMap<>(10);
        private final CuratorCache curatorCache;
        private final String path;
        private PathNode(CuratorCache curatorCache, String path) {
            this.curatorCache = curatorCache;
            this.path = path;
        }
        private void updateListenScope(String scope, String path,
                WeakReference<BaseEventSender> weakSender) {
            NodeInfo nodeInfo = queueMap.computeIfAbsent(scope, (k) -> {
                switch (scope) {
                case "ALL":
                    return addAllListenScope();
                case "CHILD":
                    return addChildListenScope();
                case "CHANGED":
                    return addChangedListenScope();
                case "CREATE":    
                    return addCreateListenScope();
                case "DELETE":
                    return addDeleteListenScope();
                case "CREATEADNCHANGED":    
                    return addCreateAndChangedListenScope();
                case "NODE":  
                    return addNodeListenScope();
                default:
                    return null;
                }
            });
            nodeInfo.nodeQueue.addSender(weakSender);
        }
        private NodeInfo addAllListenScope() {
            NodeQueue nodeQueue = new NodeQueue();
            CuratorCacheListener listener = CuratorCacheListener.builder()
                    .forAll((type, oldData, newData) -> {
                BiFunction<ChildData, ChildData, IEvent> trevent = EVENT_GETTER.get(type);
                if (null == trevent) {
                    return;
                }
                nodeQueue.checks((sender) -> {
                    sender.send(trevent.apply(oldData, newData));
                });
            }).build();
            NodeInfo nodeInfo = new NodeInfo(listener, nodeQueue);
            curatorCache.listenable().addListener(listener);
            return nodeInfo;
        }
        private NodeInfo addChildListenScope() {
            NodeQueue nodeQueue = new NodeQueue();
            CuratorCacheListener listener = CuratorCacheListener.builder()
                    .forPathChildrenCache(path, client, (curator, event) -> {
                BiFunction<ChildData, ChildData, IEvent> trevent = CHILD_EVENT_GETTER.get(event.getType());
                if (null == trevent) {
                    return;
                }
                nodeQueue.checks((sender) -> {
                    sender.send(trevent.apply(null, event.getData()));
                });
            }).build();
            NodeInfo nodeInfo = new NodeInfo(listener, nodeQueue);
            curatorCache.listenable().addListener(listener);
            return nodeInfo;
        }
        private NodeInfo addChangedListenScope() {
            NodeQueue nodeQueue = new NodeQueue();
            CuratorCacheListener listener = CuratorCacheListener.builder()
                    .forChanges((oldData, newData) -> {
                BiFunction<ChildData, ChildData, IEvent> trevent =
                        EVENT_GETTER
                        .get(org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type
                        .NODE_CHANGED);
                if (null == trevent) {
                    return;
                }
                nodeQueue.checks((sender) -> {
                    sender.send(trevent.apply(oldData, newData));
                });
            }).build();
            NodeInfo nodeInfo = new NodeInfo(listener, nodeQueue);
            curatorCache.listenable().addListener(listener);
            return nodeInfo;
        }
        private NodeInfo addCreateListenScope() {
            NodeQueue nodeQueue = new NodeQueue();
            CuratorCacheListener listener = CuratorCacheListener.builder()
                    .forCreates((newData) -> {
                BiFunction<ChildData, ChildData, IEvent> trevent =
                        EVENT_GETTER
                        .get(org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type
                        .NODE_CREATED);
                if (null == trevent) {
                    return;
                }
                nodeQueue.checks((sender) -> {
                    sender.send(trevent.apply(null, newData));
                });
            }).build();
            NodeInfo nodeInfo = new NodeInfo(listener, nodeQueue);
            curatorCache.listenable().addListener(listener);
            return nodeInfo;
        }
        private NodeInfo addDeleteListenScope() {
            NodeQueue nodeQueue = new NodeQueue();
            CuratorCacheListener listener = CuratorCacheListener.builder()
                    .forDeletes((newData) -> {
                BiFunction<ChildData, ChildData, IEvent> trevent =
                        EVENT_GETTER
                        .get(org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type
                        .NODE_DELETED);
                if (null == trevent) {
                    return;
                }
                nodeQueue.checks((sender) -> {
                    sender.send(trevent.apply(null, newData));
                });
            }).build();
            NodeInfo nodeInfo = new NodeInfo(listener, nodeQueue);
            curatorCache.listenable().addListener(listener);
            return nodeInfo;
        }
        private NodeInfo addCreateAndChangedListenScope() {
            NodeQueue nodeQueue = new NodeQueue();
            CuratorCacheListener listener = CuratorCacheListener.builder()
                    .forCreatesAndChanges((oldData, newData) -> {
                org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type typeNow;
                if (null == oldData) {
                    typeNow = org.apache.curator.framework.recipes.cache.CuratorCacheListener
                            .Type.NODE_CREATED; 
                } else {
                    typeNow = org.apache.curator.framework.recipes.cache.CuratorCacheListener
                            .Type.NODE_CHANGED; 
                }
                BiFunction<ChildData, ChildData, IEvent> trevent = EVENT_GETTER.get(typeNow);
                if (null == trevent) {
                    return;
                }
                nodeQueue.checks((sender) -> {
                    sender.send(trevent.apply(oldData, newData));
                });
            }).build();
            NodeInfo nodeInfo = new NodeInfo(listener, nodeQueue);
            curatorCache.listenable().addListener(listener);
            return nodeInfo;
        }
        private NodeInfo addNodeListenScope() {
            NodeQueue nodeQueue = new NodeQueue();
            CuratorCacheListener listener = CuratorCacheListener.builder()
                    .forNodeCache(() -> {
                BiFunction<ChildData, ChildData, IEvent> trevent =
                        EVENT_GETTER
                        .get(org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type.NODE_CHANGED);
                if (null == trevent) {
                    return;
                }
                nodeQueue.checks((sender) -> {
                    sender.send(trevent.apply(null, null));
                });
            }).build();
            NodeInfo nodeInfo = new NodeInfo(listener, nodeQueue);
            curatorCache.listenable().addListener(listener);
            return nodeInfo;
        }
        private boolean isNodeInfoEmpty() {
            Iterator<Map.Entry<String, NodeInfo>> iterator = queueMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, NodeInfo> entry = iterator.next();
                NodeInfo nodeInfo = entry.getValue();
                if (nodeInfo.nodeQueue.checks(null)) {
                    iterator.remove();
                    curatorCache.listenable().removeListener(nodeInfo.listener);
                    String scope = entry.getKey();
                    LOGGER.info("PathManager.pathNode:{} remove scope:{}!", path, scope);
                }
            }
            return queueMap.isEmpty();
        }
    }

    /**
     * Add to send
     */
    private Map<String, PathNode> pathNodeMap = new HashMap<>(1000);
    private synchronized CuratorCache getPathNode(String scope, String path, WeakReference<BaseEventSender> weakSender) {
        PathNode pathNode = pathNodeMap.computeIfAbsent(path, (k) -> {
            CuratorCache curatorCache = CuratorCache.build(client, path);
            curatorCache.start();
            return new PathNode(curatorCache, path);
        });
        pathNode.updateListenScope(scope, path, weakSender);
        return pathNode.curatorCache;
    }

    /**
     * Periodic scheduling of garbage removal
     */
    private synchronized void deleteNodes() {
        Iterator<Map.Entry<String, PathNode>> iterator = pathNodeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PathNode> entry = iterator.next();
            PathNode pathNode = entry.getValue();
            if (pathNode.isNodeInfoEmpty()) {
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
    public static abstract class BaseEventSender extends GameOver {
        abstract void send(IEvent event);
    }

    public CuratorCache addPathListener(String scopePath, BaseEventSender sender) {
        String scope = null;
        String path = null;
        String[] strArray = scopePath.split(":");
        switch (strArray.length) {
        case 1:
            path = strArray[0];
            scope = "ALL";
            break;
        case 2:
            path = strArray[1];
            scope = strArray[0];
            break;
        default:
            throw new RuntimeException("scopePath error!");
        }
        WeakReference<BaseEventSender> weakSender = new WeakReference<>(sender);
        return getPathNode(scope, path, weakSender);
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
