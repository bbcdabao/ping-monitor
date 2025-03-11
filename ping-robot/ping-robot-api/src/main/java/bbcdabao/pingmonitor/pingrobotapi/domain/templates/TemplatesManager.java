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

package bbcdabao.pingmonitor.pingrobotapi.domain.templates;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.domain.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.domain.extraction.ExtractionField;
import bbcdabao.pingmonitor.common.infra.SpringContextHolder;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;
import bbcdabao.pingmonitor.pingrobotapi.infra.configs.RobotConfig;

/**
 * Template core management
 */
public class TemplatesManager {

    private static class Holder {
        private static final TemplatesManager INSTANCE = getTemplatesManagerInstance();
    }

    private static TemplatesManager getTemplatesManagerInstance() {
        TemplatesManager templatesManager = new TemplatesManager();
        try {
            String plugPath = SpringContextHolder
                    .getBean(RobotConfig.class).getPlugsPath();
            if (ObjectUtils.isEmpty(plugPath)) {
                throw new Exception("plugPath is isEmpty!");
            }
            Reflections reflections = new Reflections(plugPath);

            Set<Class<? extends IPingMoniterPlug>> pingMoniterPlugs = reflections.getSubTypesOf(IPingMoniterPlug.class);
            if (CollectionUtils.isEmpty(pingMoniterPlugs)) {
                throw new Exception("no ping moniter plug exit!");
            }
            for (Class<? extends IPingMoniterPlug> pingMoniterPlug : pingMoniterPlugs) {
                String plugName = pingMoniterPlug.getName().replace('.', '_');
                templatesManager.pingMoniterPlugMap.put(plugName, pingMoniterPlug);
            }
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
        return templatesManager;
    }

    public static TemplatesManager getInstance() {
        return Holder.INSTANCE;
    }

    private Map<String, Class<? extends IPingMoniterPlug>> pingMoniterPlugMap = new HashMap<>(20);

    private TemplatesManager() {
    }

    public void checkPingMoniterPlug(ICheck check) {
        for (Map.Entry<String, Class<? extends IPingMoniterPlug>> entry : pingMoniterPlugMap.entrySet()) {
            Class<? extends IPingMoniterPlug> plugClazz = entry.getValue();
            String plugName = entry.getKey();
            try {
                check.onCheck(plugName, plugClazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public IPingMoniterPlug getPingMoniterPlug(String plugName) throws Exception {
        Class<? extends IPingMoniterPlug> plugClazz = pingMoniterPlugMap.get(plugName);
        if (null == plugClazz) {
            throw new NoPlugFoundException(plugName);
        }
        return plugClazz.getConstructor().newInstance();
    }

    public IPingMoniterPlug getPingMoniterPlugUsedTaskName(String taskName) throws Exception {
        String plugName = CoordinationManager.getInstance().getPlugNameByTaskName(taskName);
        Properties properties = CoordinationManager.getInstance().getTaskConfigByTaskName(taskName);
        IPingMoniterPlug pingMoniterPlug = getPingMoniterPlug(plugName);
        ExtractionField.getInstance().populateObjectFromProperties(properties, pingMoniterPlug);
        return pingMoniterPlug;
    }
}
