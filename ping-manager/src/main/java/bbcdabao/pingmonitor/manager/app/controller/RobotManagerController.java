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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import bbcdabao.pingmonitor.manager.app.module.RobotInstanceInfo;
import bbcdabao.pingmonitor.manager.app.module.RobotTaskInfo;
import bbcdabao.pingmonitor.manager.app.services.IRobotManager;
import bbcdabao.pingmonitor.manager.app.services.sse.BaseSseSession;
import bbcdabao.pingmonitor.manager.app.services.sse.sessions.RobotInstancesSession;
import bbcdabao.pingmonitor.manager.app.services.sse.sessions.RobotMasterInstancesSession;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/robot")
public class RobotManagerController {

    private final Logger logger = LoggerFactory.getLogger(RobotManagerController.class);

    @Autowired
    private IRobotManager robotManager;

    @GetMapping(value = "/{robotGroupName}/instances/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public void getRobotGroupNameInstancesForSse (
            @PathVariable("robotGroupName") String robotGroupName,
            HttpServletResponse response) throws Exception {
        logger.info("enter:getinstances:{}", robotGroupName);
        BaseSseSession.startProcess(() -> {
            return new RobotInstancesSession(robotGroupName, response);
        });
    }

    @GetMapping(value = "/{robotGroupName}/masterinstances/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public void getRobotGroupNameMasterInstancesForSse (
            @PathVariable("robotGroupName") String robotGroupName,
            HttpServletResponse response) throws Exception {
        logger.info("enter:getinstances:{}", robotGroupName);
        BaseSseSession.startProcess(() -> {
            return new RobotMasterInstancesSession(robotGroupName, response);
        });
    }

    @GetMapping(value = "/{robotGroupName}/instances")
    @ResponseBody
    public ResponseEntity<Collection<RobotInstanceInfo>> getRobotGroupNameInstances (
            @PathVariable("robotGroupName") String robotGroupName) throws Exception {
        ResponseEntity<Collection<RobotInstanceInfo>> response = ResponseEntity.ok(
                robotManager.getInstances(robotGroupName));
        return response;
    }

    @GetMapping(value = "/instances")
    @ResponseBody
    public ResponseEntity<Collection<RobotInstanceInfo>> getInstances () throws Exception {
        ResponseEntity<Collection<RobotInstanceInfo>> response = ResponseEntity.ok(
                robotManager.getInstances(null));
        return response;
    }

    @GetMapping(value = "/{robotGroupName}/tasks")
    @ResponseBody
    public ResponseEntity<Collection<RobotTaskInfo>> getRobotGroupNameTasks (
            @PathVariable("robotGroupName") String robotGroupName) throws Exception {
        ResponseEntity<Collection<RobotTaskInfo>> response = ResponseEntity.ok(
                robotManager.getTasks(robotGroupName));
        return response;
    }

    @GetMapping(value = "/tasks")
    @ResponseBody
    public ResponseEntity<Collection<RobotTaskInfo>> getTasks () throws Exception {
        ResponseEntity<Collection<RobotTaskInfo>> response = ResponseEntity.ok(
                robotManager.getTasks(null));
        return response;
    }
}