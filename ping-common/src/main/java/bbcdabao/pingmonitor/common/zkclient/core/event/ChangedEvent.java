package bbcdabao.pingmonitor.common.zkclient.core.event;

import org.apache.curator.framework.recipes.cache.ChildData;

import lombok.Data;

@Data
public class ChangedEvent implements IEvent {
    private ChildData oldData;
    private ChildData data;
}
