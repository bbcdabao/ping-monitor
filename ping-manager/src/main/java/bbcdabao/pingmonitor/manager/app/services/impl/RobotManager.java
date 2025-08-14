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

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.infra.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.common.infra.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.infra.dataconver.IConvertFromByte;
import bbcdabao.pingmonitor.common.infra.json.JsonConvert;
import bbcdabao.pingmonitor.manager.app.module.responses.RobotGroupInfo;
import bbcdabao.pingmonitor.manager.app.module.responses.RobotInstanceInfo;
import bbcdabao.pingmonitor.manager.app.services.IRobotManager;

@Service
public class RobotManager implements IRobotManager {

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
    public Collection<RobotInstanceInfo> getRobotInstanceInfos(String robotGroupName) throws Exception {
        Collection<RobotInstanceInfo> robotInstanceInfos = new ArrayList<>();
        if (ObjectUtils.isEmpty(robotGroupName)) {
            return robotInstanceInfos;
        }
        IConvertFromByte<String> convertFromByteForString =
                ByteDataConver.getInstance().getConvertFromByteForString();
        IPath robotMetaInfoInstancePath = IPath.robotMetaInfoInstancePath(robotGroupName);
        CoordinationManager cm = CoordinationManager.getInstance();
        cm.getChildren(robotMetaInfoInstancePath, (IPath childPath, String robotUUID, byte[] data) -> {
            String robotInfo = convertFromByteForString.getValue(data);
            RobotInstanceInfo robotInstanceInfo = new RobotInstanceInfo();
            robotInstanceInfo.setRobotGroupName(robotGroupName);
            robotInstanceInfo.setRobotUUID(robotUUID);
            robotInstanceInfo.setRobotInfo(robotInfo);
            robotInstanceInfos.add(robotInstanceInfo);
        });
        return robotInstanceInfos;
    }

    @Override
    public Collection<String> getRobotGroupTasks(String robotGroupName) throws Exception {
        if (ObjectUtils.isEmpty(robotGroupName)) {
            return new ArrayList<>();
        }
        IPath robotMetaInfoTaskPath = IPath.robotMetaInfoTaskPath(robotGroupName);
        CoordinationManager cm = CoordinationManager.getInstance();
        return cm.getChildren(robotMetaInfoTaskPath);
    }
}