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

package bbcdabao.pingmonitor.pingrobotapi.templates;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.reflections.Reflections;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.constants.PatchConstant;
import bbcdabao.pingmonitor.common.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.extraction.ExtractionField;
import bbcdabao.pingmonitor.common.extraction.TemplateField;
import bbcdabao.pingmonitor.common.json.JsonConvert;
import bbcdabao.pingmonitor.common.zkclientframe.core.CuratorFrameworkInstance;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;
import bbcdabao.pingmonitor.pingrobotapi.config.StartUpConfig;

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
            StartUpConfig startUpConfig = StartUpConfig.getInstance();
            if (null == startUpConfig) {
                throw new Exception("startUpConfig is null!");
            }
            String plugPath = startUpConfig.getPlugsPath();
            if (ObjectUtils.isEmpty(plugPath)) {
                throw new Exception("plugPath is isEmpty!");
            }
            Reflections reflections = new Reflections(StartUpConfig.getInstance().getPlugsPath());
            
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

    private void init() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkInstance.getInstance();
        for (Map.Entry<String, Class<? extends IPingMoniterPlug>> entry : pingMoniterPlugMap.entrySet()) {
            Class<? extends IPingMoniterPlug> plugClazz = entry.getValue();
            IPingMoniterPlug plug = plugClazz.getConstructor().newInstance();
            Map<String, TemplateField> plugTemplate = ExtractionField.getInstance()
                    .extractionTemplateMapFromObject(plug);
            String strPlugTemplate = JsonConvert.getInstance().tobeJson(plugTemplate);
            byte[] bytePlugTemplate = ByteDataConver.getInstance().getConvertToByteForString().getData(strPlugTemplate);
            StringBuilder sb = new StringBuilder().append(PatchConstant.ROBOT_TEMPLATES).append("/")
                    .append(entry.getKey());
            String plugNamePath = sb.toString();
            try {
                curatorFramework.create().creatingParentsIfNeeded().forPath(plugNamePath, bytePlugTemplate);
            } catch (NodeExistsException e) {
                curatorFramework.setData().forPath(plugNamePath, bytePlugTemplate);
            }
        }
    }

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
        StringBuilder sb = new StringBuilder().append(PatchConstant.TASKS).append("/").append(taskName);
        String plugName = ByteDataConver.getInstance().getConvertFromByteForString()
                .getValue(CuratorFrameworkInstance.getInstance().getData().forPath(sb.toString()));

        sb.append(PatchConstant.CONFIG);

        Properties properties = ByteDataConver.getInstance().getConvertFromByteForProperties()
                .getValue(CuratorFrameworkInstance.getInstance().getData().forPath(sb.toString()));

        IPingMoniterPlug pingMoniterPlug = getPingMoniterPlug(plugName);

        ExtractionField.getInstance().populateObjectFromProperties(properties, pingMoniterPlug);

        return pingMoniterPlug;
    }
}
