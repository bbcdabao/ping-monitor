# ping-monitor

# Text description of the dial test system idea：
- The dial test system is a distributed application, based on zookeeper coordination. The zk data structure is as follows
The following is based on a zk namespace
```
/sysconfig
  └── (JSON format system configuration) "{pingcycle: 60000}"

/robot (Robot root directory)
  ├── /templates (Robot plugin templates)
  │   ├── /com_xxx_sss_PingCallTest
  │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
  │   ├── /com_xxx_sss_HttpCallTest
  │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, url: http://test.com}"
  │   ├── /com_xxx_sss_XXXXCallTest
  │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, calres: http://a.com}"
  ├── /register (Robot registration directory)
  │   ├── /rebot-xxx (Robot group name)
  │   │   ├──meta-info (Robot and task inf)
  │   │   │   ├── /instance (Instance child nodes, all temporary nodes)
  │   │   │   │   ├── /UUID01 ("ip@procid")
  │   │   │   │   ├── /UUID02 ("ip@procid")
  │   │   │   │   └── /UUID03 ("ip@procid")
  │   │   │   ├── /tasks (Monitoring task list, child nodes must be unique)
  │   │   │   │   ├── /task-01 (Scheduling concurrency configuration)
  │   │   │   │   └── /task-02 (Scheduling concurrency configuration)
  │   │   ├──run-info (Runing controle info)
  │   │   │   ├── /election (Robot instance election)
  │   │   │   ├── /task-fire (Task trigger)
  │   │   │   │   ├── /task-01
  │   │   │   │   ├── /task-02
  │   │   │   ├── /task-avge (Avg child nodes, all temporary nodes)
  │   │   │   │   ├── /UUID01 ("ip@procid")
  │   │   │   │   │   ├──/task-02
  │   │   │   │   ├── /UUID02 ("ip@procid")
  │   │   │   │   │   ├──/task-02
  │   │   │   │   └── /UUID03 ("ip@procid")

/tasks (Task configuration)
  ├── /task-01 (Robot plugin template: com_xxx_sss_PingCallTest)
  │   └── /config (Properties format) "{ip=127.0.0.1, port=3251}"
  ├── /task-02 (Robot plugin template: com_xxx_sss_HttpCallTest)
  │   └── /config (Properties format) "{url=https://baiduaa.com}"

/result (Monitoring results, child nodes have TTL)
  ├── /task-01
  │   ├── /rebot-xxx (300ms)
  │   └── /rebot-xxx (300ms)
  ├── /task-02
  │   ├── /rebot-xxx (300ms)
  │   └── /rebot-xxx (500ms)

```

# 拨测系统思路文字描述：
- 拨测系统是一个分布式应用，基于zookeeper协调，zk数据结构如下
下面是基于一个zk namespace:
```
/sysconfig
  └── (JSON格式系统配置) "{pingcycle: 60000}"

/robot (机器人根目录)
  ├── /templates (机器人插件模板)
  │   ├── /com_xxx_sss_PingCallTest 
  │   │     └── (JSON格式模板) "{pingTimeout: {type: LONG, desCn: 超时时间, desEn: timeout}, ipaddr: 192.168.10.8}"
  │   ├── /com_xxx_sss_HttpCallTest 
  │   │     └── (JSON格式模板) "{pingTimeout: {type: LONG, desCn: 超时时间, desEn: timeout}, url: http://test.com}"
  │   ├── /com_xxx_sss_XXXXCallTest 
  │   │     └── (JSON格式模板) "{pingTimeout: {type: LONG, desCn: 超时时间, desEn: timeout}, calres: http://a.com}"
  ├── /register (机器人注册目录)
  │   ├── /rebot-xxx (机器人组名称)
  │   │   ├──meta-info (Robot and task inf)
  │   │   │   ├── /instance (实例子节点，都是临时节点)
  │   │   │   │   ├── /UUID01 ("ip@procid")
  │   │   │   │   ├── /UUID02 ("ip@procid")
  │   │   │   │   └── /UUID03 ("ip@procid")
  │   │   │   ├── /tasks (拨测任务列表，子节点不可重复)
  │   │   │   │   ├── /task-01 (调度并发配置)
  │   │   │   │   └── /task-02 (调度并发配置)
  │   │   ├──run-info (运行调度控制信息)
  │   │   │   ├── /election (各个实例选主)
  │   │   │   ├── /task-fire (分派触发调度)
  │   │   │   │   ├── /task-01
  │   │   │   │   ├── /task-02
  │   │   │   ├── /task-avge (各个实例平均分配策略，子节点，都是临时节点)
  │   │   │   │   ├── /UUID01 ("ip@procid")
  │   │   │   │   │   ├──/task-02
  │   │   │   │   ├── /UUID02 ("ip@procid")
  │   │   │   │   │   ├──/task-01
  │   │   │   │   └── /UUID03 ("ip@procid")

/tasks (任务配置)
  ├── /task-01 (机器人插件模板: com_xxx_sss_PingCallTest)
  │   └── /config (Properties格式) "{ip=127.0.0.1, port=3251}"
  ├── /task-02 (机器人插件模板: com_xxx_sss_HttpCallTest)
  │   └── /config (Properties格式) "{url=https://baiduaa.com}"

/result (拨测结果，子结点带有TTL)
  ├── /task-01
  │   ├── /rebot-xxx (300ms)
  │   └── /rebot-xxx (300ms)
  ├── /task-02
  │   ├── /rebot-xxx (300ms)
  │   └── /rebot-xxx (500ms)
```

- Code module design, the following structure (chinese:下面是代码结构设计)
```
ping-monitor
│ 
│    
├── ping-common         // Common part code
│   ├── src
│   └── pom.xml
│ 
├── ping-manager        // Dial and test background services, built-in zookeeper facilitates development and testing
│   ├── src
│   └── pom.xml
│ 
├── ping-manager-web    // Dial test background service, API gateway
│   ├── src
│   └── pom.xml
│ 
├── ping-manager-ui     // Dial and test background services, built-in zookeeper facilitates development and testing
│   ├── src
│   └── package.json
│ 
├── ping-metric-exporter// Dial test results to monitor perception and export to Prometheus
│   ├── src
│   └── pom.xml
│
└── ping-robot          // Dial test robot software development kit
    │ 
    ├── ping-robot-api  // Support robot group development components
    │   ├── src 
    │   └── pom.xml
    │ 
    ├── ping-robot-man  // Dial test robot implementation
    │   ├── src 
    │   └── pom.xml
    │ 
    └── pom.xml
```



import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

public class CuratorCacheExample {
    public static void main(String[] args) throws Exception {
        String connectString = "localhost:2181";  // 替换成你的 Zookeeper 地址
        String watchPath = "/stub/mtk"; // 需要监听的路径

        // 创建 CuratorFramework 客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                connectString,
                new ExponentialBackoffRetry(1000, 3)
        );
        client.start();

        // 创建 CuratorCache 监听 /stub/mtk 目录
        CuratorCache cache = CuratorCache.builder(client, watchPath).build();

        // 添加 CuratorCacheListener 监听子节点变化
        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forPathChildrenCache(watchPath, client, (curator, event) -> {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            System.out.println("子节点添加: " + event.getData().getPath());
                            break;
                        case CHILD_UPDATED:
                            System.out.println("子节点更新: " + event.getData().getPath());
                            break;
                        case CHILD_REMOVED:
                            System.out.println("子节点删除: " + event.getData().getPath());
                            break;
                        default:
                            break;
                    }
                }).build();

        // 绑定监听器
        cache.listenable().addListener(listener);

        // 启动 CuratorCache
        cache.start();
        System.out.println("CuratorCache 监听启动，监听路径: " + watchPath);

        // 阻止主线程退出
        Thread.sleep(Long.MAX_VALUE);
    }
}



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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import bbcdabao.pingmonitor.common.domain.extraction.FieldType;
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

    private static enum CtMode {
        ALL(0, "ALL"),
        CHILD(1, "CHILD");

        private final int index;
        private final String ct;
        CtMode(int index, String ct) {
            this.index = index;
            this.ct = ct;
        }
        private static final Map<String, CtMode> CTMODE_MAP = new HashMap<>(9);
        static {
            CTMODE_MAP.put("ALL", ALL);
            CTMODE_MAP.put("CHILD", CHILD);
        }
        public static CtMode getCtMode(String ct) {
            return CTMODE_MAP.get(ct);
        }
    }

    private final CuratorFramework client;
    private final long scanCycle;

    private static final Map<org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type, BiFunction<ChildData, ChildData, IEvent>> EVENT_GETTER = new HashMap<>(3);
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
    
    private static final Map<org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type, BiFunction<ChildData, ChildData, IEvent>> CHILD_EVENT_GETTER = new HashMap<>(3);
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

    /**
     * To share monitoring
     */
    private class PathNode {
        private final LinkedList<WeakReference<BaseEventSender>> queueEvent = new LinkedList<>();
        private final LinkedList<WeakReference<BaseEventSender>>[] queueSub = new LinkedList[9];
        private final CuratorCache curatorCache;
        private final String path;
        private PathNode(CuratorCache curatorCache, String path) {
            this.curatorCache = curatorCache;
            this.path = path;
        }
        private void sendEvent(CtMode ctMode, final IEvent event) {
            LinkedList<WeakReference<BaseEventSender>> queueaaa =  queueSub[ctMode.index];
        }
        private void sendEvent(final IEvent event) {
            checks((sender) -> {
                sender.send(event);
            });
        }
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
                    if (null == consumer) {
                        continue;
                    }
                    consumer.accept(sender);
                }
            }
            return queueEvent.isEmpty();
        }
        private synchronized boolean checksEx(CtMode ctMode, Consumer<BaseEventSender> consumer) {
            LinkedList<WeakReference<BaseEventSender>> queueEvent = queueSub[ctMode.index];
            if (CollectionUtils.isEmpty(queueEvent)) {
                return false;
            }
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
    private synchronized CuratorCache getPathNode(String pathCt, WeakReference<BaseEventSender> weakSender) {
        AtomicReference<String> path = new AtomicReference<>();
        String ct = null;
        String[] paths = pathCt.split(":");
        switch (paths.length) {
        case 1:
            path.set(paths[0]);
            ct = "ALL";
            break;
        case 2:
            path.set(paths[1]);
            ct = paths[0];
            break;
        default:
            throw new RuntimeException("pathCt error!");
        }
        PathNode pathNode = pathNodeMap.computeIfAbsent(path, (k) -> {
            CuratorCache curatorCache = CuratorCache.build(client, path.get());
            curatorCache.start();
            final PathNode pathNodeNew = new PathNode(curatorCache, path.get());
            CuratorCacheListener listener  = CuratorCacheListener.builder().forAll((type, oldData, newData) -> {
                BiFunction<ChildData, ChildData, IEvent> trevent = EVENT_GETTER.get(type);
                if (null == trevent) {
                    return;
                }
                pathNodeNew.sendEvent(trevent.apply(oldData, newData));
            }).build();
            CuratorCacheListener listeneraa = CuratorCacheListener.builder()
                    .forPathChildrenCache(path, client, (curator, event) -> {
                        switch (event.getType()) {
                            case CHILD_ADDED:
                                System.out.println("子节点添加: " + event.getData().getPath());
                                break;
                            case CHILD_UPDATED:
                                System.out.println("子节点更新: " + event.getData().getPath());
                                break;
                            case CHILD_REMOVED:
                                System.out.println("子节点删除: " + event.getData().getPath());
                                break;
                            default:
                                break;
                        }
                    }).build();
            curatorCache.listenable().addListener(listener);
            return pathNodeNew;
        });
        pathNode.addSender(weakSender);
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
    public static abstract class BaseEventSender extends GameOver {
        abstract void send(IEvent event);
    }

    public CuratorCache addPathListener(String pathCt, BaseEventSender sender) {
        WeakReference<BaseEventSender> weakSender = new WeakReference<>(sender);
        return getPathNode(pathCt, weakSender);
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


