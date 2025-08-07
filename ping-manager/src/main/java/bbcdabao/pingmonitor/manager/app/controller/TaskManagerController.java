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

package bbcdabao.pingmonitor.manager.app.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.zookeeper.KeeperException.NoNodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import bbcdabao.pingmonitor.manager.app.module.ApiResponse;
import bbcdabao.pingmonitor.manager.app.module.payloads.AddTaskPayload;
import bbcdabao.pingmonitor.manager.app.module.payloads.AddTaskRobotGroupsPayload;
import bbcdabao.pingmonitor.manager.app.module.responses.CheckRobotGroupInfo;
import bbcdabao.pingmonitor.manager.app.module.responses.RobotGroupInfo;
import bbcdabao.pingmonitor.manager.app.module.responses.TaskInfo;
import bbcdabao.pingmonitor.manager.app.services.ITaskManager;

@Controller
@RequestMapping("/api/tasks")
public class TaskManagerController {

    private final Logger logger = LoggerFactory.getLogger(TaskManagerController.class);

    @Autowired
    private ITaskManager taskManager;

    private Properties convertMapToProperties(Map<String, Object> map) {
        Properties properties = new Properties();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                properties.setProperty(key, value.toString());
            }
        }
        return properties;
    }

    @PostMapping(value = "/{taskName}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> tasksForPost(
            @PathVariable("taskName") String taskName,
            @RequestBody AddTaskPayload addTaskPayload) throws Exception {
        taskManager.addTask(taskName, addTaskPayload.getPlugName(),
        		convertMapToProperties(addTaskPayload.getProperties()));
        return ResponseEntity
                .ok()
                .body(ApiResponse.ok());
    }

    @DeleteMapping(value = "/{taskName}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> tasksForDelete(
            @PathVariable("taskName") String taskName) throws Exception {
        taskManager.deleteTask(taskName);
        return ResponseEntity
                .ok()
                .body(ApiResponse.ok());
    }

    @GetMapping(value = "/{taskName}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Collection<TaskInfo>>> tasksForGet(
            @PathVariable("taskName") String taskName) throws Exception {
        Collection<TaskInfo> taskInfos = new ArrayList<>();
        try {
            taskInfos = taskManager.getTasks(taskName);
        } catch (NoNodeException e) {
            logger.info("tasksForGet: no task! please create!");
        }
        return ResponseEntity
        		.ok()
        		.body(ApiResponse.ok(taskInfos));
    }

    @GetMapping(value = "")
    @ResponseBody
    public ResponseEntity<ApiResponse<Collection<TaskInfo>>> tasksForGet() throws Exception {
        Collection<TaskInfo> taskInfos = new ArrayList<>();
        try {
            taskInfos = taskManager.getTasks(null);
        } catch (NoNodeException e) {
            logger.info("tasksForGet: no task! please create!");
        }
        return ResponseEntity
                .ok()
                .body(ApiResponse.ok(taskInfos));
    }

    @GetMapping(value = "/{taskName}/robot-groups")
    @ResponseBody
    public ResponseEntity<ApiResponse<Collection<RobotGroupInfo>>> robotGroupsForGet(
            @PathVariable("taskName") String taskName) throws Exception {
        return ResponseEntity
        		.ok()
        		.body(ApiResponse.ok(taskManager.getRobotGroupInfos(taskName)));
    }
    
    @GetMapping(value = "/{taskName}/check-robot-groups")
    @ResponseBody
    public ResponseEntity<ApiResponse<Collection<CheckRobotGroupInfo>>> checkRobotGroupsForGet(
            @PathVariable("taskName") String taskName) throws Exception {
        return ResponseEntity
        		.ok()
        		.body(ApiResponse.ok(taskManager.getCheckRobotGroupInfos(taskName)));
    }
    
    @PostMapping(value = "/{taskName}/robot-groups")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> robotGroupsForPost(
            @PathVariable("taskName") String taskName,
            @RequestBody AddTaskRobotGroupsPayload addTaskRobotGroupsPayload) throws Exception {
    		taskManager.setRobotGroupInfos(taskName, addTaskRobotGroupsPayload.getRobotGroups());
    		return ResponseEntity
        		.ok()
        		.body(ApiResponse.ok());
    }

}