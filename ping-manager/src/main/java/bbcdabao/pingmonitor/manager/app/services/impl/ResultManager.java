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
import bbcdabao.pingmonitor.manager.app.module.responses.PingresultInfo;
import bbcdabao.pingmonitor.manager.app.services.IResultManager;

@Service
public class ResultManager implements IResultManager {

    private final Logger logger = LoggerFactory.getLogger(ResultManager.class);

    private PingresultInfo getOnePingresultInfo(String taskName, long durationTime) throws Exception {

        CoordinationManager cm = CoordinationManager.getInstance();
        IPath taskPath = IPath.taskPath(taskName);
        Stat stat = new Stat();
        byte[] data = cm.getData(taskPath, stat);

        long currentTimeMs = System.currentTimeMillis();
        boolean success = true;
        if (data != null && data.length > 0) {
            long elapsedMillis = currentTimeMs - stat.getMtime();
            if (elapsedMillis < durationTime) {
                success = false;
            }
        }
        PingresultInfo pingresultInfo = new PingresultInfo();
        pingresultInfo.setTaskName(taskName);
        pingresultInfo.setSuccess(success);
        return pingresultInfo;
    }

    @Override
    public Collection<PingresultInfo> getResults(String taskName, long durationTime) throws Exception {
        logger.info("ResultManager.getResults:enter:{}:{}", taskName, durationTime);
        Collection<PingresultInfo> pingresultInfos = new ArrayList<>();
        if (!ObjectUtils.isEmpty(taskName)) {
            pingresultInfos.add(getOnePingresultInfo(taskName, durationTime));
            return pingresultInfos;
        }
        long currentTimeMs = System.currentTimeMillis();
        CoordinationManager cm = CoordinationManager.getInstance();
        IPath taskPath = IPath.taskPath();
        cm.getChildren(taskPath, (IPath childPath, String child, byte[] data, Stat stat) -> {
            boolean success = true;
            if (data != null && data.length > 0) {
                long elapsedMillis = currentTimeMs - stat.getMtime();
                if (elapsedMillis < durationTime) {
                    success = false;
                }
            }
            PingresultInfo pingresultInfo = new PingresultInfo();
            pingresultInfo.setTaskName(child);
            pingresultInfo.setSuccess(success);
            pingresultInfos.add(pingresultInfo);
        });
        return pingresultInfos;
    }
}