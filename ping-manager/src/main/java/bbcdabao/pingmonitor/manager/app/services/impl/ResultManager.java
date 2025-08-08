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

package bbcdabao.pingmonitor.manager.app.services.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.infra.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.common.infra.coordination.Pingresult;
import bbcdabao.pingmonitor.common.infra.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.infra.dataconver.IConvertFromByte;
import bbcdabao.pingmonitor.common.infra.json.JsonConvert;
import bbcdabao.pingmonitor.manager.app.module.responses.PingresultInfo;
import bbcdabao.pingmonitor.manager.app.module.responses.ResultDetailInfo;
import bbcdabao.pingmonitor.manager.app.services.IResultManager;

@Service
public class ResultManager implements IResultManager {

    private final Logger logger = LoggerFactory.getLogger(ResultManager.class);

    private ResultDetailInfo getOnePingresultInfo(String taskName) throws Exception {

        CoordinationManager cm = CoordinationManager.getInstance();
        IPath taskPath = IPath.taskPath(taskName);
        Stat stat = new Stat();
        byte[] data = cm.getData(taskPath, stat);

        long currentTimeMs = System.currentTimeMillis();
        boolean success = true;

        PingresultInfo pingresultInfo = new PingresultInfo();
        //pingresultInfo.setTaskName(taskName);
        //pingresultInfo.setSuccess(success);
        return null;
    }

    @Override
    public Collection<ResultDetailInfo> getResults(String taskName) throws Exception {
        logger.info("ResultManager.getResults:enter:{}", taskName);
        Collection<ResultDetailInfo> resultDetailInfos = new ArrayList<>();
        if (!ObjectUtils.isEmpty(taskName)) {
            //resultDetailInfos.add(getOnePingresultInfo(taskName));
            return resultDetailInfos;
        }
        IConvertFromByte<String> convertFromByteForString = ByteDataConver.getInstance().getConvertFromByteForString();
        CoordinationManager cm = CoordinationManager.getInstance();
        cm.getChildren(IPath.resultPath(), (IPath resultPathInfo, String taskNameInfo) -> {
            ResultDetailInfo resultDetailInfo = new ResultDetailInfo();
            resultDetailInfo.setTaskName(taskNameInfo);
            Collection<PingresultInfo> pingresultInfos = resultDetailInfo.getPingresultInfos();
            cm.getChildren(resultPathInfo, (IPath robotGroupPathInfo, String robotGroupNameInfo, byte[] robotGroupDataInfo, Stat robotGroupStatInfo) -> {
                PingresultInfo pingresultInfo = new PingresultInfo();
                pingresultInfo.setRobotGroupName(robotGroupNameInfo);
                pingresultInfo.setTimestamp(robotGroupStatInfo.getMtime());
                Pingresult pingresult = JsonConvert.getInstance().fromJson(convertFromByteForString.getValue(robotGroupDataInfo), Pingresult.class);
                pingresultInfo.setPingresult(pingresult);
                pingresultInfos.add(pingresultInfo);
            });
            resultDetailInfos.add(resultDetailInfo);
        });
        return resultDetailInfos;
    }
}