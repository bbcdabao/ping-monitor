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
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.common.infra.coordination.Pingresult;
import bbcdabao.pingmonitor.common.infra.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.infra.dataconver.IConvertFromByte;
import bbcdabao.pingmonitor.common.infra.json.JsonConvert;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.manager.app.module.responses.PingresultInfo;
import bbcdabao.pingmonitor.manager.app.module.responses.ResultInfo;
import bbcdabao.pingmonitor.manager.app.services.sse.BaseSseSession;
import bbcdabao.pingmonitor.manager.app.services.sse.SSEvent;
import jakarta.servlet.http.HttpServletResponse;

public class PingresultinfosSession extends BaseSseSession {

    private final Logger logger = LoggerFactory.getLogger(PingresultinfosSession.class);

    private IPath path;

    private void doEvent(ChildData childData, SSEvent eventType) throws Exception {
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

        JsonConvert jsonConvert = JsonConvert.getInstance();

        String taskName = paths[2];
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setTaskName(taskName);
        if (paths.length == 3) {
            sendMessage(jsonConvert.tobeJson(resultInfo), eventType);
            logger.info("PingresultinfosSession.doEvent:{}:{}", eventType.toString(), taskName);
            return;
        }

        String robotGroupName = paths[3];

        IConvertFromByte<String> convertFromByteForString =
                ByteDataConver.getInstance().getConvertFromByteForString();

        Pingresult pingresult = jsonConvert
                .fromJson(convertFromByteForString.getValue(childData.getData()), Pingresult.class);
        
        Stat stat = childData.getStat();
        long lastModifiedTime = stat.getMtime();

        PingresultInfo pingresultInfo = new PingresultInfo();
        pingresultInfo.setRobotGroupName(robotGroupName);
        pingresultInfo.setTimestamp(lastModifiedTime);
        pingresultInfo.setPingresult(pingresult);

        resultInfo.setPingresultInfo(pingresultInfo);
        sendMessage(jsonConvert.tobeJson(resultInfo), eventType);
        logger.info("PingresultinfosSession.doEvent:{}:{}:{}", eventType.toString(), taskName, pingresultInfo.toString());
    }

    public PingresultinfosSession(HttpServletResponse response, String taskName) {
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
        doEvent(data.getData(), SSEvent.CREATE);
    }

    @Override
    public void onEvent(DeletedEvent data) throws Exception {
        doEvent(data.getData(), SSEvent.DELETE);
    }

    @Override
    public void onEvent(ChangedEvent data) throws Exception {
        doEvent(data.getData(), SSEvent.UPDATE);
    }
}