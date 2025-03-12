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

package bbcdabao.pingmonitor.pingrobotapi.app.services.impl;

import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import bbcdabao.pingmonitor.common.domain.FactoryBase;
import bbcdabao.pingmonitor.common.domain.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.domain.extraction.ExtractionField;
import bbcdabao.pingmonitor.common.domain.extraction.TemplateField;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;
import bbcdabao.pingmonitor.pingrobotapi.domain.RobotConfig;
import bbcdabao.pingmonitor.pingrobotapi.domain.templates.TemplatesManager;

public class StartUpService implements ApplicationRunner {
    private void regTemplatesInfo() throws Exception {
        TemplatesManager
        .getInstance()
        .checkPingMoniterPlug((plugName, plugClazz) -> {
            IPingMoniterPlug plug = TemplatesManager
                    .getInstance().getPingMoniterPlug(plugName);
            Map<String, TemplateField> plugTemplate = ExtractionField
                    .getInstance().extractionTemplateMapFromObject(plug);
            CoordinationManager.getInstance().setPlugTemplate(plugName, plugTemplate);
        });
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        regTemplatesInfo();
        CoordinationManager
        .getInstance()
        .regRobotInstance(FactoryBase
                .getFactory()
                .getBean(RobotConfig.class)
                .getRobotGroupName());
    }
}
