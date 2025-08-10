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
import bbcdabao.pingmonitor.manager.app.module.responses.ResultInfo;
import bbcdabao.pingmonitor.manager.app.services.IResultManager;

@Service
public class ResultManager implements IResultManager {

    private final Logger logger = LoggerFactory.getLogger(ResultManager.class);
    
    private final IConvertFromByte<String> convertFromByteForString = ByteDataConver.getInstance().getConvertFromByteForString();

    private final JsonConvert jsonConvert = JsonConvert.getInstance();
    
    private ResultDetailInfo getOneResultDetailInfo(String taskName) throws Exception {
        ResultDetailInfo resultDetailInfo = new ResultDetailInfo();
        Collection<PingresultInfo> resultDetailInfos = resultDetailInfo.getPingresultInfos();
        CoordinationManager cm = CoordinationManager.getInstance();
        IPath resultPath = IPath.resultPath(taskName);
        cm.getChildren(resultPath, (IPath robotGroupPath, String robotGroupName, byte[] robotGroupData, Stat robotGroupStat) -> {
            PingresultInfo pingresultInfo = new PingresultInfo();
            pingresultInfo.setRobotGroupName(robotGroupName);
            pingresultInfo.setTimestamp(robotGroupStat.getMtime());
            Pingresult pingresult = jsonConvert.fromJson(convertFromByteForString.getValue(robotGroupData), Pingresult.class);
            pingresultInfo.setPingresult(pingresult);
            resultDetailInfos.add(pingresultInfo);
        });
        resultDetailInfo.setTaskName(taskName);
        return resultDetailInfo;
    }

    @Override
    public Collection<ResultDetailInfo> getResultDetailInfo(String taskNameParam) throws Exception {
        logger.info("ResultManager.getResultDetailInfo:enter:{}", taskNameParam);
        Collection<ResultDetailInfo> resultDetailInfos = new ArrayList<>();
        if (!ObjectUtils.isEmpty(taskNameParam)) {
            resultDetailInfos.add(getOneResultDetailInfo(taskNameParam));
            return resultDetailInfos;
        }
        CoordinationManager cm = CoordinationManager.getInstance();
        cm.getChildren(IPath.resultPath(), (IPath resultPath, String taskName) -> {
            ResultDetailInfo resultDetailInfo = new ResultDetailInfo();
            resultDetailInfo.setPingresultInfos(null);
            resultDetailInfo.setTaskName(taskName);
            resultDetailInfos.add(resultDetailInfo);
        });
        return resultDetailInfos;
    }

    @Override
    public Collection<ResultInfo> getResultInfos() throws Exception {
        logger.info("ResultManager.getResultInfos:enter");
        Collection<ResultInfo> pesultInfos = new ArrayList<>();
        CoordinationManager cm = CoordinationManager.getInstance();
        cm.getChildren(IPath.resultPath(), (IPath resultPath, String taskName) -> {
            cm.getChildren(resultPath, (IPath pingresultPath, String robotGroupName, byte[] data, Stat stat) -> {
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setTaskName(taskName);

                PingresultInfo pingresultInfo = new PingresultInfo();
                pingresultInfo.setRobotGroupName(robotGroupName);
                pingresultInfo.setTimestamp(stat.getMtime());
                Pingresult pingresult = Pingresult.getPingresult(data);
                pingresultInfo.setPingresult(pingresult);

                resultInfo.setPingresultInfo(pingresultInfo);

                pesultInfos.add(resultInfo);
            });
        });
        return pesultInfos;
    }

    @Override
    public void deleteResults(String taskName) throws Exception {
        if (ObjectUtils.isEmpty(taskName)) {
            throw new Exception("taskName is empty!");
        }
        CoordinationManager cm = CoordinationManager.getInstance();
        cm.deleteData(IPath.resultPath(taskName));
    }
}