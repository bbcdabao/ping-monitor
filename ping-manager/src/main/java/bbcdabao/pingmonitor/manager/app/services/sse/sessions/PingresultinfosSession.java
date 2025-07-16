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
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.common.infra.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.infra.json.JsonConvert;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.DeletedEvent;
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