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

package bbcdabao.pingmonitor.manager.app.services.sse.sessions;

import bbcdabao.pingmonitor.common.domain.coordination.IPath;
import bbcdabao.pingmonitor.common.domain.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.domain.json.JsonConvert;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.manager.app.module.RobotInstanceInfo;
import bbcdabao.pingmonitor.manager.app.services.sse.BaseSseSession;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

public class RobotInstancesSession extends BaseSseSession {

    @Data
    private static class RobotInstanceInfoEvent {
        /**
         * 0 is add event
         * 1 is delete event
         */
        private int eventId;
        private RobotInstanceInfo robotInstanceInfo = new RobotInstanceInfo();
    }

    /**
     * ZOOKEEPER path /robot/register/GROUPXXX/instance
     */
    private IPath path;

    public RobotInstancesSession(String robotGroupName, HttpServletResponse response) {
        super(response);
        path = IPath.robotMetaInfoInstancePath(robotGroupName);
    }

    @Override
    public void doProcess() throws Exception {
        String pathStart = "CHILD:" + path.get();
        startBloking(pathStart);
    }

    public void onEvent(CreatedEvent data) throws Exception {
        String pathGet = data.getData().getPath();
        String instanceId = pathGet.replaceFirst(path.get(), "");
        String instanceInfo = ByteDataConver.getInstance()
                .getConvertFromByteForString().getValue(data.getData().getData());
        RobotInstanceInfoEvent event = new RobotInstanceInfoEvent();
        event.setEventId(0);
        event.robotInstanceInfo.setRobotId(instanceId);
        event.robotInstanceInfo.setRobotInfo(instanceInfo);
        sendMessage(JsonConvert.getInstance().tobeJson(event));
    }

    public void onEvent(DeletedEvent data) throws Exception {
        String pathGet = data.getData().getPath();
        String instanceId = pathGet.replaceFirst(path.get(), "");
        RobotInstanceInfoEvent event = new RobotInstanceInfoEvent();
        event.setEventId(1);
        event.robotInstanceInfo.setRobotId(instanceId);
        sendMessage(JsonConvert.getInstance().tobeJson(event));  
    }
}
