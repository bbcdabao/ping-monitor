package bbcdabao.pingmonitor.common.zkclient.core.event;

import org.apache.curator.framework.recipes.cache.ChildData;

import lombok.Data;

@Data
public class CreatedEvent implements IEvent {
    private ChildData data;
}
