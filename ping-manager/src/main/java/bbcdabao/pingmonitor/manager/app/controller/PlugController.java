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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import bbcdabao.pingmonitor.manager.app.module.PlugInfo;
import bbcdabao.pingmonitor.manager.app.services.IPlugOpt;

@Controller
@RequestMapping("/plug")
public class PlugController {

    @Autowired
    private IPlugOpt plugOpt;

    @GetMapping(value = "/{plugName}/pluginfos")
    @ResponseBody
    public ResponseEntity<Collection<PlugInfo>> getPlugNamePlugInfos(
            @PathVariable("plugName") String plugName) throws Exception {
        ResponseEntity<Collection<PlugInfo>> response = ResponseEntity.ok(
                plugOpt.getPlugInfos(plugName));
        return response;
    }

    @GetMapping(value = "/pluginfos")
    @ResponseBody
    public ResponseEntity<Collection<PlugInfo>> getPlugInfos() throws Exception {
        ResponseEntity<Collection<PlugInfo>> response = ResponseEntity.ok(
                plugOpt.getPlugInfos(null));
        return response;
    }
    
    @DeleteMapping(value = "/{plugName}")
    @ResponseBody
    public ResponseEntity<String> deletePlug(   
            @PathVariable("plugName") String plugName) throws Exception {
        plugOpt.deletePlug(plugName);
        return ResponseEntity.ok("success");
    }
}
