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

import java.util.HashMap;
import java.util.Map;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.common.infra.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.infra.dataconver.IConvertFromByte;
import bbcdabao.pingmonitor.common.infra.json.JsonConvert;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.manager.app.module.sse.ResultInfo;
import bbcdabao.pingmonitor.manager.app.services.sse.BaseSseSession;
import bbcdabao.pingmonitor.manager.app.services.sse.EventType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

public class PingresultinfosSession extends BaseSseSession {

    private final Logger logger = LoggerFactory.getLogger(PingresultinfosSession.class);

    @Data
    private static class PingresultinfoEvent {
        private EventType eventType;
        private ResultInfo resultInfo = new ResultInfo();
    }

    private IPath path;

    private void doEvent(ChildData childData, EventType eventType) throws Exception {
        if (childData == null) {
            return;
        }
        String path = childData.getPath();
        if (path == null) {
            return;
        }
        String paths[] = path.split("/");
        if (paths.length < 3) {
            return;
        }
        String taskName = paths[2];
        PingresultinfoEvent event = new PingresultinfoEvent();
        event.setEventType(eventType);
        event.getResultInfo().setTaskName(taskName);
        if (paths.length == 3) {
            sendMessage(JsonConvert.getInstance().tobeJson(event));
            return;
        }
        String robotGroupName = paths[3];
        Map<String, String> pingresultMap = new HashMap<>();
        IConvertFromByte<String> convertFromByteForString = ByteDataConver.getInstance().getConvertFromByteForString();
        pingresultMap.put(robotGroupName, convertFromByteForString.getValue(childData.getData()));
        event.getResultInfo().setPingresultMap(pingresultMap);
        sendMessage(JsonConvert.getInstance().tobeJson(event));
    }

    public PingresultinfosSession(HttpServletResponse response) {
        super(response);
        path = IPath.resultPath();
    }

    @Override
    public void doProcess() throws Exception {
        String pathStart = "CHILD:" + path.get();
        startBlokingAlone(pathStart);
    }

    @Override
    public void onEvent(CreatedEvent data) throws Exception {
        doEvent(data.getData(), EventType.CREATE);
    }

    @Override
    public void onEvent(DeletedEvent data) throws Exception {
        doEvent(data.getData(), EventType.DELETE);
    }

    @Override
    public void onEvent(ChangedEvent data) throws Exception {
        doEvent(data.getData(), EventType.UPDATE);
    }
}