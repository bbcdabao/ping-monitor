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

package bbcdabao.pingmonitor.pingrobotapi.app.services.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import bbcdabao.pingmonitor.common.domain.extraction.ExtractionField;
import bbcdabao.pingmonitor.common.domain.extraction.TemplateField;
import bbcdabao.pingmonitor.common.infra.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.infra.coordination.Sysconfig;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfig;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfigNotify;
import bbcdabao.pingmonitor.pingrobotapi.infra.RobotConfig;
import bbcdabao.pingmonitor.pingrobotapi.infra.templates.TemplatesManager;
import jakarta.annotation.PostConstruct;

/**
 * Robot startup, registration plug-in template, registration instance id.
 */
public class StartUpService extends TimeWorkerBase implements ApplicationRunner, ISysconfigNotify {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartUpService.class);

    @Autowired
    private ISysconfig regSysconfigNotify;

    @Autowired
    private RobotConfig robotConfig;

    private void regTemplatesInfo() throws Exception {
        TemplatesManager
        .getInstance()
        .checkPingMoniterPlug((plugName, plugClazz) -> {
            IPingMoniterPlug plug = TemplatesManager
                    .getInstance().getPingMoniterPlug(plugName);
            Map<String, TemplateField> plugTemplate = ExtractionField
                    .getInstance().extractionTemplateMapFromObject(plug);
        	CoordinationManager cm = CoordinationManager.getInstance();
            cm.setPlugTemplate(plugName, plugTemplate);
        });
    }

    @PostConstruct
    public void init() {
        regSysconfigNotify.reg(this);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        regTemplatesInfo();
    }

    @Override
    public void onChange(Sysconfig config) throws Exception {
        beginCron(config.getCronInst());
    }

    @Override
    public void onExecute() throws Exception {
        CoordinationManager cm = CoordinationManager.getInstance();
        cm.regRobotInstance(robotConfig.getRobotGroupName());
    }
}