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
import java.util.List;

import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.infra.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.common.infra.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.infra.dataconver.IConvertFromByte;
import bbcdabao.pingmonitor.common.infra.json.JsonConvert;
import bbcdabao.pingmonitor.manager.app.module.RobotInstanceInfo;
import bbcdabao.pingmonitor.manager.app.module.RobotTaskInfo;
import bbcdabao.pingmonitor.manager.app.module.responses.RobotGroupInfo;
import bbcdabao.pingmonitor.manager.app.services.IRobotManager;

@Service
public class RobotManager implements IRobotManager {

    private Collection<RobotInstanceInfo> getRobotInstanceInfos(String robotGroupName) throws Exception {
        Collection<RobotInstanceInfo> robotInstanceInfos = new ArrayList<>();
        CoordinationManager
        .getInstance()
        .getChildren(
                IPath.robotMetaInfoInstancePath(robotGroupName),
                (IPath childPath, String child, byte[] data) -> {
                    RobotInstanceInfo robotInstanceInfo = new RobotInstanceInfo();
                    robotInstanceInfo.setRobotGroupName(robotGroupName);
                    robotInstanceInfo.setRobotUUID(child);
                    robotInstanceInfo.setRobotInfo(
                            ByteDataConver
                            .getInstance()
                            .getConvertFromByteForString()
                            .getValue(data));
                    robotInstanceInfos.add(robotInstanceInfo);
                });
        return robotInstanceInfos;
    }

    @Override
    public Collection<RobotGroupInfo> getRobotGroupInfos(String robotGroupName) throws Exception {
    	Collection<RobotGroupInfo> robotGroupInfos = new ArrayList<>();
    	IConvertFromByte<String> convertFromByte = ByteDataConver.getInstance().getConvertFromByteForString();
    	CoordinationManager cm = CoordinationManager.getInstance();
    	if (null != robotGroupName) {
    		IPath childPath = IPath.robotRegisterPathGroup(robotGroupName);
    		byte[] data = cm.getData(childPath);
    		String infoJson = convertFromByte.getValue(data);
    		RobotGroupInfo robotGroupInfo = JsonConvert.getInstance()
    				.fromJson(infoJson, RobotGroupInfo.class);
    		robotGroupInfo.setRobotGroupName(robotGroupName);
    		robotGroupInfos.add(robotGroupInfo);
    		return robotGroupInfos;
    	}
    	cm.getChildren(IPath.robotRegisterPath(), (IPath childPath, String child, byte[] data) -> {
    		String infoJson = convertFromByte.getValue(data);
    		RobotGroupInfo robotGroupInfo = JsonConvert.getInstance()
    				.fromJson(infoJson, RobotGroupInfo.class);
    		robotGroupInfo.setRobotGroupName(child);
    		robotGroupInfos.add(robotGroupInfo);
    	});
    	return robotGroupInfos;
    }

    @Override
    public Collection<RobotInstanceInfo> getInstances(String robotGroupName) throws Exception {
        if (!ObjectUtils.isEmpty(robotGroupName)) {
            return getRobotInstanceInfos(robotGroupName);
        }
        Collection<RobotInstanceInfo> robotInstanceInfos = new ArrayList<>();
        List<String> robotGroupNames = CoordinationManager
                .getInstance()
                .getChildren(IPath.robotRegisterPath());
        for (String robotGroupNameGet : robotGroupNames) {
            Collection<RobotInstanceInfo> subRobotInstanceInfos = getRobotInstanceInfos(robotGroupNameGet);
            robotInstanceInfos.addAll(subRobotInstanceInfos);
        }
        return robotInstanceInfos;
    }

    private Collection<RobotTaskInfo> getRobotTaskInfos(String robotGroupName) throws Exception {
        Collection<RobotTaskInfo> taskInfos = new ArrayList<>();
        Collection<String> tasks = CoordinationManager
                .getInstance()
                .getChildren(IPath.robotMetaInfoTaskPath(robotGroupName));
        for (String task : tasks) {
            RobotTaskInfo robotTaskInfo = new RobotTaskInfo();
            robotTaskInfo.setRobotGroupName(robotGroupName);
            robotTaskInfo.setTaskName(task);
            taskInfos.add(robotTaskInfo);
        }
        return taskInfos;
    }

    @Override
    public Collection<RobotTaskInfo> getTasks(String robotGroupName) throws Exception {
        if (!ObjectUtils.isEmpty(robotGroupName)) {
            return getRobotTaskInfos(robotGroupName);
        }
        Collection<RobotTaskInfo> taskInfos = new ArrayList<>();
        List<String> robotGroupNames = CoordinationManager
                .getInstance()
                .getChildren(IPath.robotRegisterPath());
        for (String robotGroupNameGet : robotGroupNames) {
            Collection<RobotTaskInfo> subTaskInfos = getTasks(robotGroupNameGet);
            taskInfos.addAll(subTaskInfos);
        }
        return taskInfos;
    }
}