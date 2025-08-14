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

import org.apache.zookeeper.KeeperException.NoNodeException;
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

import bbcdabao.pingmonitor.manager.app.module.ApiResponse;
import bbcdabao.pingmonitor.manager.app.module.responses.RobotGroupInfo;
import bbcdabao.pingmonitor.manager.app.module.responses.RobotInstanceInfo;
import bbcdabao.pingmonitor.manager.app.services.IRobotManager;
import bbcdabao.pingmonitor.manager.app.services.sse.BaseSseSession;
import bbcdabao.pingmonitor.manager.app.services.sse.sessions.RobotGroupInstanceTaskInfosSession;
import bbcdabao.pingmonitor.manager.app.services.sse.sessions.RobotGroupMasterInfosSession;
import bbcdabao.pingmonitor.manager.app.services.sse.sessions.RobotGroupTasksSession;
import bbcdabao.pingmonitor.manager.app.services.sse.sessions.RobotInstancesSession;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/robot")
public class RobotManagerController {

    private final Logger logger = LoggerFactory.getLogger(RobotManagerController.class);

    @Autowired
    private IRobotManager robotManager;
    
    @GetMapping(value = "/groups")
    @ResponseBody
    public ResponseEntity<ApiResponse<Collection<RobotGroupInfo>>> robotGroupsForGet(
            ) throws Exception {
        Collection<RobotGroupInfo> robotGroupInfos = new ArrayList<>();
        try {
            robotGroupInfos = robotManager.getRobotGroupInfos(null);
        } catch (NoNodeException e) {
            logger.info("robotGroupsForGet: no robotGroups");
        }
        return ResponseEntity
        		.ok()
        		.body(ApiResponse.ok(robotGroupInfos));
    }
    
    @GetMapping(value = "/groups/{robotGroupName}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Collection<RobotGroupInfo>>> robotGroupsForGet(
            @PathVariable("robotGroupName") String robotGroupName) throws Exception {
        Collection<RobotGroupInfo> robotGroupInfos = new ArrayList<>();
        try {
            robotGroupInfos = robotManager.getRobotGroupInfos(robotGroupName);
        } catch (NoNodeException e) {
            logger.info("robotGroupsForGet: no robotGroups");
        }
        return ResponseEntity
        		.ok()
        		.body(ApiResponse.ok(robotGroupInfos));
    }

    @GetMapping(value = "/groups/{robotGroupName}/instances")
    @ResponseBody
    public ResponseEntity<ApiResponse<Collection<RobotInstanceInfo>>> robotInstanceInfoForGet(
            @PathVariable("robotGroupName") String robotGroupName) throws Exception {
        Collection<RobotInstanceInfo> robotInstanceInfos = robotManager.getRobotInstanceInfos(robotGroupName);
        return ResponseEntity
                .ok()
                .body(ApiResponse.ok(robotInstanceInfos));
    }

    @GetMapping(value = "/groups/{robotGroupName}/instances/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public void robotInstanceInfoForSse (
            @PathVariable("robotGroupName") String robotGroupName,
            HttpServletResponse response) throws Exception {
        logger.info("enter:getinstances:{}", robotGroupName);
        BaseSseSession.startProcess(() -> {
            return new RobotInstancesSession(robotGroupName, response);
        });
    }
    
    @GetMapping(value = "/groups/{robotGroupName}/tasks")
    @ResponseBody
    public ResponseEntity<ApiResponse<Collection<String>>> robotGroupTasksForGet(
            @PathVariable("robotGroupName") String robotGroupName) throws Exception {
        Collection<String> robotGroupTasks = robotManager.getRobotGroupTasks(robotGroupName);
        return ResponseEntity
                .ok()
                .body(ApiResponse.ok(robotGroupTasks));
    }
    
    @GetMapping(value = "/groups/{robotGroupName}/tasks/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public void robotGroupTasksForSse (
            @PathVariable("robotGroupName") String robotGroupName,
            HttpServletResponse response) throws Exception {
        logger.info("enter:getinstances:{}", robotGroupName);
        BaseSseSession.startProcess(() -> {
            return new RobotGroupTasksSession(robotGroupName, response);
        });
    }

    @GetMapping(value = "/groups/{robotGroupName}/master/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public void robotGroupMasterForSse (
            @PathVariable("robotGroupName") String robotGroupName,
            HttpServletResponse response) throws Exception {
        logger.info("enter:getinstances:{}", robotGroupName);
        BaseSseSession.startProcess(() -> {
            return new RobotGroupMasterInfosSession(robotGroupName, response);
        });
    }
    
    @GetMapping(value = "/groups/{robotGroupName}/instancetasks/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public void robotGroupInstanceTaskInfoForSse (
            @PathVariable("robotGroupName") String robotGroupName,
            HttpServletResponse response) throws Exception {
        logger.info("enter:getinstances:{}", robotGroupName);
        BaseSseSession.startProcess(() -> {
            return new RobotGroupInstanceTaskInfosSession(robotGroupName, response);
        });
    }
}