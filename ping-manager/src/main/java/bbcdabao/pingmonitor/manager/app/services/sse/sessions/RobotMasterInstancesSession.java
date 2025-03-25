package bbcdabao.pingmonitor.manager.app.services.sse.sessions;

import bbcdabao.pingmonitor.common.domain.coordination.IPath;
import bbcdabao.pingmonitor.common.domain.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.domain.json.JsonConvert;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.manager.app.services.sse.BaseSseSession;
import bbcdabao.pingmonitor.manager.app.services.sse.EventType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

public class RobotMasterInstancesSession extends BaseSseSession {

    @Data
    private static class RobotMasterInstanceInfoEvent {
        private EventType eventType;
        private String masterRobotInfo;
    }

    private IPath path;

    public RobotMasterInstancesSession(String robotGroupName, HttpServletResponse response) {
        super(response);
        path = IPath.robotRunInfoMasterInstancePath(robotGroupName);
    }

    @Override
    public void doProcess() throws Exception {
        String pathStart = "CHILD:" + path.get();
        startBlokingAlone(pathStart);
    }

    @Override
    public void onEvent(CreatedEvent data) throws Exception {
        String masterRobotInfo = ByteDataConver.getInstance()
                .getConvertFromByteForString().getValue(data.getData().getData());
        RobotMasterInstanceInfoEvent event = new RobotMasterInstanceInfoEvent();
        event.setEventType(EventType.CREATE);
        event.setMasterRobotInfo(masterRobotInfo);
        sendMessage(JsonConvert.getInstance().tobeJson(event));
    }

    @Override
    public void onEvent(DeletedEvent data) throws Exception {
        String masterRobotInfo = ByteDataConver.getInstance()
                .getConvertFromByteForString().getValue(data.getData().getData());
        RobotMasterInstanceInfoEvent event = new RobotMasterInstanceInfoEvent();
        event.setEventType(EventType.DELETE);
        event.setMasterRobotInfo(masterRobotInfo);
        sendMessage(JsonConvert.getInstance().tobeJson(event)); 
    }

    @Override
    public void onEvent(ChangedEvent data) throws Exception {
        String masterRobotInfo = ByteDataConver.getInstance()
                .getConvertFromByteForString().getValue(data.getData().getData());
        RobotMasterInstanceInfoEvent event = new RobotMasterInstanceInfoEvent();
        event.setEventType(EventType.UPDATE);
        event.setMasterRobotInfo(masterRobotInfo);
        sendMessage(JsonConvert.getInstance().tobeJson(event)); 
    }
}
