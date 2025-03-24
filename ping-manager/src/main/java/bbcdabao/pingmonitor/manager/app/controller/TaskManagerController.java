/*
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

package bbcdabao.pingmonitor.manager.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import bbcdabao.pingmonitor.common.domain.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.domain.coordination.IPath;
import bbcdabao.pingmonitor.common.domain.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.manager.app.module.TaskInfo;

@Controller
@RequestMapping("/task")
public class TaskManagerController {
    @PutMapping(value = "/{taskName}/plug/{plugName}")
    @ResponseBody
    public ResponseEntity<String> addTask(
            @PathVariable("taskName") String taskName,
            @PathVariable("plugName") String plugName,
            @RequestBody Properties properties) throws Exception {
        CoordinationManager.getInstance().createTask(taskName, plugName, properties);
        return ResponseEntity.ok("success");
    }
    @DeleteMapping(value = "/{taskName}")
    @ResponseBody
    public ResponseEntity<String> deleteTask(
            @PathVariable("taskName") String taskName) throws Exception {
        CoordinationManager.getInstance().deleteData(IPath.taskPath(taskName));
        return ResponseEntity.ok("Task deleted successfully");
    }
    @PostMapping(value = "/{taskName}")
    @ResponseBody
    public ResponseEntity<String> updateTask(
            @PathVariable("taskName") String taskName,
            @RequestBody Properties properties) throws Exception {
        CoordinationManager.getInstance().setTaskConfigByTaskName(taskName, properties);
        return ResponseEntity.ok("success");
    }
    @GetMapping(value = "/{taskName}")
    @ResponseBody
    public ResponseEntity<TaskInfo> getOneTask(
            @PathVariable("taskName") String taskName) throws Exception {
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
        ResponseEntity<TaskInfo> response = ResponseEntity.ok(taskInfo);
        return response;
    }
    @GetMapping(value = "")
    @ResponseBody
    public ResponseEntity<List<TaskInfo>> getTask() throws Exception {
        CoordinationManager cm = CoordinationManager.getInstance();
        ByteDataConver bd = ByteDataConver.getInstance();
        List<TaskInfo> taskInfos = new ArrayList<>();
        cm.getChildren(IPath.taskPath(), (IPath childPath, String child, byte[] data) -> {
            try {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setPlugName(bd
                        .getConvertFromByteForString()
                        .getValue(data));
                IPath childConfig = IPath.taskConfigPath(child);
                taskInfo.setConfig(bd
                        .getConvertFromByteForProperties()
                        .getValue(cm
                                .getData(childConfig)));
                
            } catch (Exception e) {
            }
        });
        ResponseEntity<List<TaskInfo>> response = ResponseEntity.ok(taskInfos);
        return response;
    }
}
