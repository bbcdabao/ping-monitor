/**
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

import org.apache.curator.utils.ZKPaths;

import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.common.infra.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.manager.app.module.RobotInstanceInfo;
import bbcdabao.pingmonitor.manager.app.services.sse.BaseSseSession;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

public class RobotInstancesSession extends BaseSseSession {

    @Data
    private static class RobotInstanceInfoEvent {
        private RobotInstanceInfo robotInstanceInfo = new RobotInstanceInfo();
    }

    private IPath path;

    public RobotInstancesSession(String robotGroupName, HttpServletResponse response) {
        super(response);
        path = IPath.robotMetaInfoInstancePath(robotGroupName);
    }

    @Override
    public void doProcess() throws Exception {
        String pathStart = "CHILD:" + path.get();
        startBlokingAlone(pathStart);
    }

    @Override
    public void onEvent(CreatedEvent data) throws Exception {
        String instanceInfo = ByteDataConver.getInstance()
                .getConvertFromByteForString().getValue(data.getData().getData());

        RobotInstanceInfoEvent event = new RobotInstanceInfoEvent();
        event.robotInstanceInfo.setRobotUUID(ZKPaths.getNodeFromPath(data.getData().getPath()));
        event.robotInstanceInfo.setRobotInfo(instanceInfo);
    }

    @Override
    public void onEvent(DeletedEvent data) throws Exception {
        RobotInstanceInfoEvent event = new RobotInstanceInfoEvent();
        event.robotInstanceInfo.setRobotUUID(ZKPaths.getNodeFromPath(data.getData().getPath()));
    }
}