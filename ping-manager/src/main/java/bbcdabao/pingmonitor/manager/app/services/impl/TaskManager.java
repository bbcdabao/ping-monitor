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
import java.util.Properties;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.infra.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.common.infra.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.manager.app.module.TaskInfo;
import bbcdabao.pingmonitor.manager.app.services.ITaskManager;

@Service
public class TaskManager implements ITaskManager {
    private TaskInfo getOneTask(String taskName) throws Exception {
        CoordinationManager cm = CoordinationManager.getInstance();
        ByteDataConver bd = ByteDataConver.getInstance();
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setPlugName(bd
                .getConvertFromByteForString()
                .getValue(cm
                        .getData(IPath.taskPath(taskName))));
        taskInfo.setConfig(bd
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
    public Collection<TaskInfo> getTask(String taskName) throws Exception {
        Collection<TaskInfo> taskInfos = new ArrayList<>();
        if (!ObjectUtils.isEmpty(taskName)) {
            taskInfos.add(getOneTask(taskName));
            return taskInfos;
        }
        CoordinationManager cm = CoordinationManager.getInstance();
        List<String> taskNames = cm.getChildren(IPath.taskPath());
        for (String taskNameGet: taskNames) {
            taskInfos.add(getOneTask(taskNameGet));
        }
        return taskInfos;
    }
}