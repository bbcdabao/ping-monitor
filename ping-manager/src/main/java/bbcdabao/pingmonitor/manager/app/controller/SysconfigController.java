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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import bbcdabao.pingmonitor.common.infra.coordination.Sysconfig;
import bbcdabao.pingmonitor.manager.app.module.ApiResponse;
import bbcdabao.pingmonitor.manager.app.module.payloads.SysconfigPayload;
import bbcdabao.pingmonitor.manager.app.module.responses.SysconfigInfo;
import bbcdabao.pingmonitor.manager.app.services.ISysconfig;

@Controller
@RequestMapping("/api/sysconfig")
public class SysconfigController {

    @Autowired
    private ISysconfig sysconfigOpt;

    @GetMapping(value = "")
    @ResponseBody
    public ResponseEntity<ApiResponse<SysconfigInfo>> getConfig (
            ) throws Exception {
        SysconfigInfo sysconfigInfo = new SysconfigInfo();
        Sysconfig sysconfig = sysconfigOpt.getConfig();
        sysconfigInfo.setCronTask(sysconfig.getCronTask());
        sysconfigInfo.setCronInst(sysconfig.getCronInst());
        sysconfigInfo.setCronMain(sysconfig.getCronMain());
        sysconfigInfo.setTimeOutMs(sysconfig.getTimeOutMs());
        sysconfigInfo.setRsType(sysconfig.getRsType().getValue());
        return ResponseEntity
                .ok()
                .body(ApiResponse.ok(sysconfigInfo));
    }

    @PostMapping(value = "")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> updateConfig(
            @RequestBody SysconfigPayload sysconfigPayload) throws Exception {
        Sysconfig sysconfig = sysconfigOpt.getConfig();
        sysconfig.setCronInst(sysconfigPayload.getCronInst());
        sysconfig.setCronMain(sysconfigPayload.getCronMain());
        sysconfig.setCronTask(sysconfigPayload.getCronTask());
        sysconfig.setTimeOutMs(sysconfigPayload.getTimeOutMs());
        sysconfigOpt.setConfig(sysconfig);
        return ResponseEntity
                .ok()
                .body(ApiResponse.ok());
    }
}