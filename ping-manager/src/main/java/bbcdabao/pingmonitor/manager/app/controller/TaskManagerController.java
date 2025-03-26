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

import java.util.Collection;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
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

import bbcdabao.pingmonitor.manager.app.module.TaskInfo;
import bbcdabao.pingmonitor.manager.app.services.ITaskManager;

@Controller
@RequestMapping("/task")
public class TaskManagerController {

    @Autowired
    private ITaskManager taskManager;

    @PutMapping(value = "/{taskName}/plug/{plugName}")
    @ResponseBody
    public ResponseEntity<String> addTask(
            @PathVariable("taskName") String taskName,
            @PathVariable("plugName") String plugName,
            @RequestBody Properties properties) throws Exception {
        taskManager.addTask(taskName, plugName, properties);
        return ResponseEntity.ok("success");
    }
    @DeleteMapping(value = "/{taskName}")
    @ResponseBody
    public ResponseEntity<String> deleteTask(
            @PathVariable("taskName") String taskName) throws Exception {
        taskManager.deleteTask(taskName);
        return ResponseEntity.ok("success");
    }
    @PostMapping(value = "/{taskName}")
    @ResponseBody
    public ResponseEntity<String> updateTask(
            @PathVariable("taskName") String taskName,
            @RequestBody Properties properties) throws Exception {
        taskManager.updateTask(taskName, properties);
        return ResponseEntity.ok("success");
    }
    @GetMapping(value = "/{taskName}/taskInfos")
    @ResponseBody
    public ResponseEntity<Collection<TaskInfo>> getOneTask(
            @PathVariable("taskName") String taskName) throws Exception {
        ResponseEntity<Collection<TaskInfo>> response = ResponseEntity.ok(taskManager.getTask(taskName));
        return response;
    }
    @GetMapping(value = "/taskInfos")
    @ResponseBody
    public ResponseEntity<Collection<TaskInfo>> getTask() throws Exception {
        ResponseEntity<Collection<TaskInfo>> response = ResponseEntity.ok(taskManager.getTask(null));
        return response;
    }
}
