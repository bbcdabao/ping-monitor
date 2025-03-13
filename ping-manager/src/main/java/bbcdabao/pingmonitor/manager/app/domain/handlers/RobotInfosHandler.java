package bbcdabao.pingmonitor.manager.app.domain.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import bbcdabao.pingmonitor.common.domain.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.domain.json.JsonConvert;
import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

public class RobotInfosHandler extends BaseEventHandler {
    @Data
    private static class RobotInfo {
        private int eventId;
        private String robotId;
        private String robotInfo;
    }
    private ServletOutputStream outputStream;
    public RobotInfosHandler(String robotGroupName, HttpServletResponse response) {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
            gameOver();
            return;
        }
        String patch = new StringBuilder()
                .append("/robot/register/")
                .append(robotGroupName)
                .append("/meta-info/instance")
                .toString();
        start(patch);
    }
    public void onEvent(CreatedEvent data) throws Exception {
        RobotInfo robotInfo = new RobotInfo();
        robotInfo.setEventId(1);
        robotInfo.setRobotId(data.getData().getPath());
        robotInfo.setRobotId(ByteDataConver.getInstance().getConvertFromByteForString().getValue(data.getData().getData()));
        String jsonData = JsonConvert.getInstance().tobeJson(robotInfo);
        try {
            outputStream.write(jsonData.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            gameOver();
        }        
    }
    public void onEvent(DeletedEvent data) throws Exception {
        RobotInfo robotInfo = new RobotInfo();
        robotInfo.setEventId(2);
        robotInfo.setRobotId(data.getData().getPath());
        String jsonData = JsonConvert.getInstance().tobeJson(robotInfo);
        try {
            outputStream.write(jsonData.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            gameOver();
        }  
    }
    public void onIdl() throws Exception {
        String jsonData = "\n\n";
        try {
            outputStream.write(jsonData.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            gameOver();
        }
    }
}
