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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import bbcdabao.pingmonitor.manager.app.module.ApiResponse;
import bbcdabao.pingmonitor.manager.app.module.responses.ResultDetailInfo;
import bbcdabao.pingmonitor.manager.app.module.responses.ResultInfo;
import bbcdabao.pingmonitor.manager.app.services.IResultManager;
import bbcdabao.pingmonitor.manager.app.services.sse.BaseSseSession;
import bbcdabao.pingmonitor.manager.app.services.sse.sessions.PingresultinfosSession;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/result")
public class ResultController {

    private final Logger logger = LoggerFactory.getLogger(ResultController.class);

    @Autowired
    private IResultManager resultManager;

    @GetMapping(value = "/events/{taskName}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public void getTaskNamePingresultinfosForSse(
            @PathVariable("taskName") String taskName,
            HttpServletResponse response) throws Exception {
        BaseSseSession.startProcess(() -> {
            return new PingresultinfosSession(response, taskName);
        });
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public void pingresultinfosForSse(
            HttpServletResponse response) throws Exception {
        BaseSseSession.startProcess(() -> {
            return new PingresultinfosSession(response, null);
        });
    }
    
    @GetMapping(value = "")
    @ResponseBody
    ResponseEntity<ApiResponse<Collection<ResultInfo>>> getResultInfos(
            ) throws Exception {
        return ResponseEntity
                .ok()
                .body(ApiResponse.ok(resultManager.getResultInfos()));
    }
    
    @GetMapping(value = "/details/{taskName}")
    @ResponseBody
    ResponseEntity<ApiResponse<Collection<ResultDetailInfo>>> getTaskNameResultDetailInfo(
            @PathVariable("taskName") String taskName) throws Exception {
        return ResponseEntity
                .ok()
                .body(ApiResponse.ok(resultManager.getResultDetailInfo(taskName)));
    }

    @DeleteMapping(value = "/details/{taskName}")
    @ResponseBody
    ResponseEntity<ApiResponse<Void>> deleteTaskNameResultDetailInfo(
            @PathVariable("taskName") String taskName) throws Exception {
        resultManager.deleteResults(taskName);
        return ResponseEntity
                .ok()
                .body(ApiResponse.ok());
    }

    @GetMapping(value = "/details")
    @ResponseBody
    ResponseEntity<ApiResponse<Collection<ResultDetailInfo>>> getResultDetailInfo(
            ) throws Exception {
        Collection<ResultDetailInfo> resultDetailInfos = new ArrayList<>();
        try {
            resultDetailInfos = resultManager.getResultDetailInfo(null);
        } catch (Exception e) {
            logger.info("ResultController.getResultDetailInfo error");
        }
        return ResponseEntity
                .ok()
                .body(ApiResponse.ok(resultDetailInfos));
    }
}