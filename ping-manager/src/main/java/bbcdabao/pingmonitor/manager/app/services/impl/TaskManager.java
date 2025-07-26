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
import java.util.Properties;
import java.util.Set;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.infra.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.common.infra.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.infra.dataconver.IConvertFromByte;
import bbcdabao.pingmonitor.common.infra.json.JsonConvert;
import bbcdabao.pingmonitor.manager.app.module.responses.CheckRobotGroupInfo;
import bbcdabao.pingmonitor.manager.app.module.responses.RobotGroupInfo;
import bbcdabao.pingmonitor.manager.app.module.responses.TaskInfo;
import bbcdabao.pingmonitor.manager.app.services.ITaskManager;

@Service
public class TaskManager implements ITaskManager {

	private final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private TaskInfo getOneTask(String taskName) throws Exception {
        CoordinationManager cm = CoordinationManager.getInstance();
        ByteDataConver bd = ByteDataConver.getInstance();
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setTaskName(taskName);
        taskInfo.setPlugName(bd
                .getConvertFromByteForString()
                .getValue(cm
                        .getData(IPath.taskPath(taskName))));
        taskInfo.setProperties(bd
                .getConvertFromByteForProperties()
                .getValue(cm
                        .getData(IPath.taskConfigPath(taskName))));
        return taskInfo;
    }
    
    @Override
    public void addTask(String taskName, String plugName, Properties properties) throws Exception {
        CoordinationManager.getInstance().createTask(taskName, plugName, properties);
    }
    @Override
    public void deleteTask(String taskName) throws Exception {
        CoordinationManager.getInstance().deleteData(IPath.taskPath(taskName));
    }
    @Override
    public void updateTask(String taskName, Properties properties) throws Exception {
        CoordinationManager.getInstance().setTaskConfigByTaskName(taskName, properties);
    }
    @Override
    public Collection<TaskInfo> getTasks(String taskName) throws Exception {
        Collection<TaskInfo> taskInfos = new ArrayList<>();
        if (!ObjectUtils.isEmpty(taskName)) {
            taskInfos.add(getOneTask(taskName));
            return taskInfos;
        }
        CoordinationManager cm = CoordinationManager.getInstance();
        ByteDataConver bd = ByteDataConver.getInstance();
        cm.getChildren(IPath.taskPath(), (IPath childPath, String child, byte[] data) ->{
        	String plugName = bd.getConvertFromByteForString().getValue(data);
        	TaskInfo taskInfo = new TaskInfo();
        	taskInfo.setTaskName(child);
        	taskInfo.setPlugName(plugName);
        	taskInfos.add(taskInfo);
        });
        return taskInfos;
    }
    @Override
    public Collection<RobotGroupInfo> getRobotGroupInfos(String taskName) throws Exception {
	    	Collection<RobotGroupInfo> robotGroupInfos = new ArrayList<>();
	    	IConvertFromByte<String> convertFromByte = ByteDataConver.getInstance().getConvertFromByteForString();
	    	CoordinationManager cm = CoordinationManager.getInstance();
	    	cm.getChildren(IPath.robotRegisterPath(), (IPath childPath, String child, byte[] data) -> {
	    		Stat stat = cm.getStat(IPath.robotMetaInfoTaskPath(child, taskName));
	    		if (null == stat) {
	    			return;
	    		}
	    		String infoJson = convertFromByte.getValue(data);
	    		RobotGroupInfo robotGroupInfo = JsonConvert.getInstance()
	    				.fromJson(infoJson, RobotGroupInfo.class);
	    		robotGroupInfo.setRobotGroupName(child);
	    		robotGroupInfos.add(robotGroupInfo);
	    	});
	    	return robotGroupInfos;
    }
    @Override
    public Collection<CheckRobotGroupInfo> getCheckRobotGroupInfos(String taskName) throws Exception {
    		Collection<CheckRobotGroupInfo> checkRobotGroupInfos = new ArrayList<>();
	    	IConvertFromByte<String> convertFromByte = ByteDataConver.getInstance().getConvertFromByteForString();
	    	CoordinationManager cm = CoordinationManager.getInstance();
	    	cm.getChildren(IPath.robotRegisterPath(), (IPath childPath, String child, byte[] data) -> {
	    		String infoJson = convertFromByte.getValue(data);
	    		RobotGroupInfo robotGroupInfo = JsonConvert.getInstance()
	    				.fromJson(infoJson, RobotGroupInfo.class);
	    		robotGroupInfo.setRobotGroupName(child);
	    		CheckRobotGroupInfo checkRobotGroupInfo = new CheckRobotGroupInfo();
	    		checkRobotGroupInfo.setRobotGroupInfo(robotGroupInfo);
	    		Stat stat = cm.getStat(IPath.robotMetaInfoTaskPath(child, taskName));
	    		if (null == stat) {
	    			checkRobotGroupInfo.setIscheck(false);
	    		} else {
	    			checkRobotGroupInfo.setIscheck(true);
	    		}
	    		checkRobotGroupInfos.add(checkRobotGroupInfo);
	    	});
    		return checkRobotGroupInfos;
    }
    @Override
    public void setRobotGroupInfos(String taskName, Set<String> robotGroupNameSet) throws Exception {
	    	logger.info("TaskManager.setRobotGroupInfos:{}:{}", taskName, robotGroupNameSet);
	    	CoordinationManager cm = CoordinationManager.getInstance();
	    	cm.getChildren(IPath.robotRegisterPath(), (IPath childPath, String child, byte[] data) -> {
	    		IPath indexPath = IPath.robotMetaInfoTaskPath(child, taskName);
	    		if (robotGroupNameSet.contains(child)) {
	    			logger.info("TaskManager.setRobotGroupInfos:set path:{}", indexPath.get());
	        		Stat stat = cm.getStat(indexPath);
	        		if (null == stat) {
	        			cm.createData(indexPath, CreateMode.PERSISTENT);
	        		}
	    		} else {
	        		Stat stat = cm.getStat(indexPath);
	        		if (null != stat) {
	        			cm.deleteData(indexPath);
	        		}
	    		}
	    	});
    }
}