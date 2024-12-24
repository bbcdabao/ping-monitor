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

import bbcdabao.pingmonitor.common.zkclient.event.ChangedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.CreatedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.DeletedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.IEvent;

public class PathManager implements Runnable {
    private final CuratorFramework client;

    private class PathNode {
        private final LinkedList<WeakReference<IEventSender>> queueEvent = new LinkedList<>();
        private final CuratorCache curatorCache;
        private final String path;
        private PathNode(CuratorCache curatorCache, String path) {
            this.curatorCache = curatorCache;
            this.path = path;
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


    private PathNode getPathNode(String path) {
        CuratorCache curatorCache = CuratorCache.build(client, path);
        curatorCache.start();
        PathNode pathNode = new PathNode(curatorCache, path);
        CuratorCacheListener listener  = CuratorCacheListener.builder().forAll((type, oldData, newData) -> {
            switch (type) {
            case NODE_CREATED:
                pathNode.checks((sender) -> {
                    sender.send(new CreatedEvent(newData));
                });
                break;
            case NODE_CHANGED:
                pathNode.checks((sender) -> {
                    sender.send(new ChangedEvent(oldData, newData));
                });
                break;
            case NODE_DELETED:
                pathNode.checks((sender) -> {
                    sender.send(new DeletedEvent(oldData));
                });
                break;
            }
        }).build();
        curatorCache.listenable().addListener(listener);
        return pathNode;
    }

    private Map<String, PathNode> pathNodeMap = new HashMap<>(1000);
    private synchronized void getPathNode(String path, WeakReference<IEventSender> weakSender) {
        PathNode pathNode = pathNodeMap.computeIfAbsent(path, (k) -> {
            return getPathNode(path);
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
            if (!pathNode.checks(null)) {
                continue;
            }
            iterator.remove();
            try (pathNode.curatorCache) {
            } catch (Exception e) {   
            }
        }
    }

    public static interface IEventSender {
        void send(IEvent event);
    }

    public void addPathListener(String path, IEventSender sender) {
        WeakReference<IEventSender> weakSender = new WeakReference<>(sender);
        getPathNode(path, weakSender);
    }

    public PathManager(CuratorFramework client) {
        this.client = client;
    }

    @Override
    public void run() {
        while(true) {
            deleteNodes();
            try {
                Thread.sleep(10000);
            } catch(Exception e) {
            }
        }
    }
}
