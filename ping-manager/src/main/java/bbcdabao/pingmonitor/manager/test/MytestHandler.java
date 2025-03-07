package bbcdabao.pingmonitor.manager.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;

public class MytestHandler extends BaseEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MytestHandler.class);
    public void onEvent(CreatedEvent data) throws Exception {
        LOGGER.info("CreatedEvent:{}", data.getData().getPath());
    }
    public void onEvent(ChangedEvent data) throws Exception {
        LOGGER.info("ChangedEvent:{}", data.getData().getPath());
    }
    public void onEvent(DeletedEvent data) throws Exception {
        LOGGER.info("DeletedEvent:{}", data.getData().getPath());
    }
}
