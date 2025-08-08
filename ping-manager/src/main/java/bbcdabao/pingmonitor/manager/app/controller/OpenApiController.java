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
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bbcdabao.pingmonitor.common.app.module.TokenPayload;
import bbcdabao.pingmonitor.common.app.services.IMakeTokenOpt;
import bbcdabao.pingmonitor.manager.app.module.ApiResponse;
import bbcdabao.pingmonitor.manager.app.module.payloads.UsernameLoginPayload;
import bbcdabao.pingmonitor.manager.app.module.responses.LoginResponse;
import jakarta.annotation.Resource;

/**
 * 不鉴权接口
 */
@RestController
@RequestMapping("/openapi")
public class OpenApiController {
    
    @Resource(name = "userInfoMap")
    private Map<String, String> userInfoMap;

    @Autowired
    private IMakeTokenOpt jwtService;

    @PostMapping("/usernamelogin")
    public ResponseEntity<ApiResponse<LoginResponse>> usernameloginForPost(
            @RequestBody UsernameLoginPayload payload) throws Exception {
        String username = payload.getUsername();
        String password = userInfoMap.get(username);
        if (null == password) {
            throw new RuntimeException("username error");
        }
        if (!password.equals(payload.getPassword())) {
            throw new RuntimeException("password error");
        }
        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setId(username);
        List<String> permissions = new ArrayList<>();
        tokenPayload.setPermissions(permissions);
        String ptToken = jwtService.makeToken(tokenPayload);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "ptToken=" + ptToken + "; Path=/api; HttpOnly");
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(username);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(ApiResponse.ok(loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logoutForPost(
            )throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie",
                "ptToken=; Path=/api; HttpOnly; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(ApiResponse.ok());
    }
}