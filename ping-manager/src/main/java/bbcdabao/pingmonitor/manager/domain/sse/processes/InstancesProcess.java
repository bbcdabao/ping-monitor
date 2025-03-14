/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package bbcdabao.pingmonitor.manager.domain.sse.processes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import bbcdabao.pingmonitor.common.domain.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.domain.json.JsonConvert;
import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.manager.domain.utils.SseUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

public class InstancesProcess extends BaseEventHandler {

    @Data
    private static class RobotInfo {
        private int eventId;
        private String robotId;
        private String robotInfo = "none";
    }

    private String path;
    private ServletOutputStream outputStream;

    /**
     * zookeeper path /robot/register/groupxxx/instance
     * @param robotGroupName
     * @param response
     */
    public InstancesProcess(String robotGroupName, HttpServletResponse response) {
        path = new StringBuilder()
                .append("/robot/register/")
                .append(robotGroupName)
                .append("/meta-info/instance")
                .toString();
        SseUtil.initSseHttpServletResponse(response);
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
            gameOver();
            return;
        }
    }

    public void doRun() {
        startBloking(path);
    }
    public void onEvent(CreatedEvent data) throws Exception {
        String pathGet = data.getData().getPath();
        if (path.equals(pathGet)) {
            return;
        }
        String instanceId = pathGet.replaceFirst(path, "");
        String instanceInfo = ByteDataConver.getInstance().getConvertFromByteForString().getValue(data.getData().getData());
        RobotInfo robotInfo = new RobotInfo();
        robotInfo.setEventId(1);
        robotInfo.setRobotId(instanceId);
        robotInfo.setRobotInfo(instanceInfo);
        String jsonData = JsonConvert.getInstance().tobeJson(robotInfo);
        jsonData += SseUtil.SSE_SEP;
        try {
            outputStream.write(jsonData.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            gameOver();
        }        
    }
    public void onEvent(DeletedEvent data) throws Exception {
        String pathGet = data.getData().getPath();
        if (path.equals(pathGet)) {
            return;
        }
        String instanceId = pathGet.replaceFirst(path, "");
        RobotInfo robotInfo = new RobotInfo();
        robotInfo.setEventId(2);
        robotInfo.setRobotId(instanceId);
        String jsonData = JsonConvert.getInstance().tobeJson(robotInfo);
        jsonData += SseUtil.SSE_SEP;
        try {
            outputStream.write(jsonData.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            gameOver();
        }  
    }
    public void onIdl() throws Exception {
        String jsonData = SseUtil.SSE_SEP;
        try {
            outputStream.write(jsonData.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            gameOver();
        }
    }
}
