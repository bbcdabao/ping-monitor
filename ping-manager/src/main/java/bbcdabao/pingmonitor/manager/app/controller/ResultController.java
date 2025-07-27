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

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import bbcdabao.pingmonitor.manager.app.module.responses.PingresultInfo;
import bbcdabao.pingmonitor.manager.app.services.sse.BaseSseSession;
import bbcdabao.pingmonitor.manager.app.services.sse.sessions.PingresultinfosSession;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/results")
public class ResultController {

    @GetMapping(value = "/{taskName}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public void getTaskNamePingresultinfosForSse (
            @PathVariable("taskName") String taskName,
            HttpServletResponse response) throws Exception {
        BaseSseSession.startProcess(() -> {
            return new PingresultinfosSession(taskName, response);
        });
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public void pingresultinfosForSse (
            HttpServletResponse response) throws Exception {
        BaseSseSession.startProcess(() -> {
            return new PingresultinfosSession(null, response);
        });
    }

    @GetMapping(value = "/{taskName}")
    @ResponseBody
    ResponseEntity<Collection<PingresultInfo>> getTaskNamePingresultinfos(
            @PathVariable("taskName") String taskName) throws Exception {
        return null;
    }

    @GetMapping(value = "")
    @ResponseBody
    ResponseEntity<Collection<PingresultInfo>> getPingresultinfos() throws Exception {
        return null;
    }
}