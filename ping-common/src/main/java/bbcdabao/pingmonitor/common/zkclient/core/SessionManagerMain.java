package bbcdabao.pingmonitor.common.zkclient.core;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;

import bbcdabao.pingmonitor.common.zkclient.BaseEventHandler;
import bbcdabao.pingmonitor.common.zkclient.core.PathManager.IEventSender;
import bbcdabao.pingmonitor.common.zkclient.event.ChangedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.CreatedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.DeletedEvent;
import bbcdabao.pingmonitor.common.zkclient.event.IEvent;

public class SessionManagerMain {

    private final ExecutorService executorService;

    private final CuratorFramework client;
    private final PathManager pathManager;

    private final int qeCapacity = 1000;
    private final long scanCycle = 2000;

    public SessionManagerMain(ExecutorService executorService, CuratorFramework client, long paybackcycle) {
        this.executorService = executorService;
        this.client = client;
        this.pathManager = new PathManager(client, paybackcycle);

        executorService.execute(pathManager);
    }


    
    private class HandlerNode implements Runnable, IEventSender {
        private final WeakReference<BaseEventHandler> weakhandler;
        private final LinkedBlockingQueue<IEvent> eventsQueue = new LinkedBlockingQueue<>(qeCapacity\);

        private HandlerNode(WeakReference<BaseEventHandler> weakhandler) {
            this.weakhandler = weakhandler;
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
                // info: log error
            }
        }

        @Override
        public void run() {
            while (true) {


            }
        } 
    }
}
