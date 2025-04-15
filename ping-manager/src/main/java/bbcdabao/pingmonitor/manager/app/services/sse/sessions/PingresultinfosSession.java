package bbcdabao.pingmonitor.manager.app.services.sse.sessions;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.domain.coordination.IPath;
import bbcdabao.pingmonitor.common.domain.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.domain.json.JsonConvert;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.manager.app.module.PingresultInfo;
import bbcdabao.pingmonitor.manager.app.services.sse.BaseSseSession;
import bbcdabao.pingmonitor.manager.app.services.sse.EventType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

public class PingresultinfosSession extends BaseSseSession {

    @Data
    private static class PingresultinfoEvent {
        private EventType eventType;
        private String pingresultInfo;
    }

    private IPath path;

    public PingresultinfosSession(String taskName, HttpServletResponse response) {
        super(response);
        if (ObjectUtils.isEmpty(taskName)) {
            path = IPath.resultPath();
        } else {
            path = IPath.resultPath(taskName);
        }
    }

    @Override
    public void doProcess() throws Exception {
        String pathStart = "CHILD:" + path.get();
        startBlokingAlone(pathStart);
    }

    @Override
    public void onEvent(CreatedEvent data) throws Exception {
        ChildData childData = data.getData();
        if (childData == null || childData.getData() == null || childData.getData().length == 0) {
            return;
        }
        String pingresultInfo = ByteDataConver.getInstance()
                .getConvertFromByteForString().getValue(data.getData().getData());
        PingresultinfoEvent event = new PingresultinfoEvent();
        event.setEventType(EventType.CREATE);
        event.setPingresultInfo(pingresultInfo);
        sendMessage(JsonConvert.getInstance().tobeJson(event));
    }

    @Override
    public void onEvent(DeletedEvent data) throws Exception {
        ChildData childData = data.getData();

        PingresultinfoEvent event = new PingresultinfoEvent();
        event.setEventType(EventType.DELETE);
        event.setPingresultInfo("none");
        sendMessage(JsonConvert.getInstance().tobeJson(event));
    }

    @Override
    public void onEvent(ChangedEvent data) throws Exception {

    }
}
