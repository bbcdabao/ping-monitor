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

import org.apache.curator.framework.recipes.cache.ChildData;

import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.common.infra.json.JsonConvert;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.manager.app.module.responses.RobotGroupInstanceTaskInfo;
import bbcdabao.pingmonitor.manager.app.services.sse.BaseSseSession;
import bbcdabao.pingmonitor.manager.app.services.sse.SSEvent;
import jakarta.servlet.http.HttpServletResponse;

public class RobotGroupInstanceTaskInfosSession  extends BaseSseSession {

    private final IPath path;

    private void doEvent(ChildData childData, SSEvent eventType) throws Exception {
        if (childData == null) {
            return;
        }
        String path = childData.getPath();
        if (path == null) {
            return;
        }
        String paths[] = path.split("/");
        if (paths.length != 8) {
            return;
        }
        String robotUUID = paths[6];
        String taskName = paths[7];
        RobotGroupInstanceTaskInfo robotGroupInstanceTaskInfo = new RobotGroupInstanceTaskInfo();
        robotGroupInstanceTaskInfo.setRobotUUID(robotUUID);
        robotGroupInstanceTaskInfo.setTaskName(taskName);
        JsonConvert jsonConvert = JsonConvert.getInstance();
        sendMessage(jsonConvert.tobeJson(robotGroupInstanceTaskInfo), eventType);
    }

    public RobotGroupInstanceTaskInfosSession(String robotGroupName, HttpServletResponse response) {
        super(response);
        path = IPath.robotRunInfoTaskPath(robotGroupName);
    }

    @Override
    public void doProcess() throws Exception {
        String pathStart = "CHILD:" + path.get();
        startBlokingAlone(pathStart);
    }

    @Override
    public void onEvent(CreatedEvent data) throws Exception {
        ChildData childData = data.getData();
        doEvent(childData, SSEvent.CREATE);
    }

    @Override
    public void onEvent(DeletedEvent data) throws Exception {
        ChildData childData = data.getData();
        doEvent(childData, SSEvent.DELETE);
    }
}
