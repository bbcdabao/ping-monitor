package bbcdabao.pingmonitor.common.zkclient.core;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import bbcdabao.pingmonitor.common.zkclient.BaseEventHandler;
import bbcdabao.pingmonitor.common.zkclient.event.ChangedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.CreatedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.DeletedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.IEvent;

public class SessionClinetWorker implements Runnable {

    public static SessionClinetWorker getInstance(int eventQueueSize, ExecutorService executor) throws Exception {
        if (null == executor) {
            throw new Exception("executor param is null!");
        }
        SessionClinetWorker sessionClinetWorker = new SessionClinetWorker(eventQueueSize, executor);
        executor.execute(sessionClinetWorker);
        return sessionClinetWorker;
    }

    private final int eventQueueSize;
    private final long poolTimeOut = 2000;

    private final ExecutorService executor;
    private SessionClinetWorker(int eventQueueSize, ExecutorService executor) {
        this.eventQueueSize = eventQueueSize;
        this.executor = executor;
    }

    @FunctionalInterface
    private static interface IEventSender {
        void postEvent(IEvent event);
    }

    private Map<String, 路径缓存> sessionNodeMap = new HashMap<>(100);
    private Map<Long, SessionNode> sessionNodeMap = new HashMap<>(100);

    private class SessionNode implements Runnable {
        private final WeakReference<BaseEventHandler> weakhandler;
        private final LinkedBlockingQueue<IEvent> eventQueue = new LinkedBlockingQueue<>(eventQueueSize);
        private SessionNode(WeakReference<BaseEventHandler> weakhandler) {
            this.weakhandler = weakhandler;
        }
        private boolean doEvent(IEvent event) {
            BaseEventHandler handler = weakhandler.get();
            if (null == handler) {
                return false;
            }
            try {
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
            catch (Exception e) {
            }
            return true;
        }
        @Override
        public void run() {
            while (true) {
                IEvent event = null;
                try {
                    event = eventQueue.poll(poolTimeOut, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException e) {
                    break;
                }
                if (!doEvent(event)) {
                    break;
                }
            }
        } 
    }

    @Override
    public void run() {
        // 检测回收
    }
}
