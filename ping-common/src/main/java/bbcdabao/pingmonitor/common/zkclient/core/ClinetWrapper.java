package bbcdabao.pingmonitor.common.zkclient.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;

public class ClinetWrapper implements Runnable {

    private class Node {
        private CuratorCache cache;
        private Collection<WeakReference<IEventReciver>> recivers = new ArrayList<>();
        private boolean isCheckRecivers() {
            Iterator<WeakReference<IEventReciver>> iterator = recivers.iterator();
            while (iterator.hasNext()) {
                WeakReference<IEventReciver> weakRef = iterator.next();
                IEventReciver receiver = weakRef.get();
                if (receiver == null) {
                    iterator.remove();
                }
            }
            return recivers.isEmpty();
        }
    }

    private Map<String, Node> nodeMap = new HashMap<>(1000);

    private CuratorFramework client;


    /**
     * Active session garbage collection
     */
    @Override
    public void run() {
        
    }

    public boolean register(String path, IEventReciver reciver) {

        return true;
    }
}
