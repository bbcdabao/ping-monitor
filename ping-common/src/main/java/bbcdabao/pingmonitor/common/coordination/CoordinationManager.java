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

package bbcdabao.pingmonitor.common.coordination;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import bbcdabao.pingmonitor.common.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.extraction.TemplateField;
import bbcdabao.pingmonitor.common.json.JsonConvert;
import bbcdabao.pingmonitor.common.zkclientframe.core.CuratorFrameworkInstance;

/**
 *
 * Distributed coordination
 * /sysconfig
 * └── (JSON format system configuration) "{pingcycle: 60000}"
 * 
 * /robot (Robot root directory)
 * ├── /templates (Robot plugin templates)
 * │   ├── /com_xxx_sss_PingCallTest
 * │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
 * │   ├── /com_xxx_sss_HttpCallTest
 * │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, url: [http://test.com}](http://test.com})"
 * │   ├── /com_xxx_sss_XXXXCallTest
 * │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, calres: [http://a.com}](http://a.com})"
 * ├── /register (Robot registration directory)
 * │   ├── /rebot-xxx (Robot group name)
 * │   │   ├──meta-info (Robot and task inf)
 * │   │   │   ├── /instance (Instance child nodes, all temporary nodes)
 * │   │   │   │   ├── /UUID01 ("ip@procid")
 * │   │   │   │   ├── /UUID02 ("ip@procid")
 * │   │   │   │   └── /UUID03 ("ip@procid")
 * │   │   │   ├── /tasks (Monitoring task list, child nodes must be unique)
 * │   │   │   │   ├── /task-01 (Scheduling concurrency configuration)
 * │   │   │   │   └── /task-02 (Scheduling concurrency configuration)
 * │   │   ├──run-info (Running control info)
 * │   │   │   ├── /election (Robot instance election)
 * │   │   │   ├── /task-fire (Task trigger)
 * │   │   │   │   ├── /task-01
 * │   │   │   │   ├── /task-02
 * │   │   │   ├── /task-avge (Avg child nodes, all temporary nodes)
 * │   │   │   │   ├── /UUID01 ("ip@procid")
 * │   │   │   │   │   ├── /task-02
 * │   │   │   │   ├── /UUID02 ("ip@procid")
 * │   │   │   │   │   ├── /task-02
 * │   │   │   │   └── /UUID03 ("ip@procid")
 * 
 * /tasks (Task configuration)
 * ├── /task-01 (Robot plugin template: com_xxx_sss_PingCallTest)
 * │   └── /config (Properties format) "{ip=127.0.0.1, port=3251}"
 * ├── /task-02 (Robot plugin template: com_xxx_sss_HttpCallTest)
 * │   └── /config (Properties format) "{url=[https://baiduaa.com}](https://baiduaa.com})"
 * 
 * /result (Monitoring results, child nodes have TTL)
 * ├── /task-01
 * │   ├── /rebot-xxx (300ms)
 * │   └── /rebot-xxx (300ms)
 * ├── /task-02
 * │   ├── /rebot-xxx (300ms)
 * │   └── /rebot-xxx (500ms)
 *
 */

public class CoordinationManager {

    private static class Holder {
        private static final CoordinationManager INSTANCE = new CoordinationManager();
    }

    public static CoordinationManager getInstance() {
        return Holder.INSTANCE;
    }

    private CoordinationManager() {
    }

    private void createOrSetData(String path, CreateMode mode, byte[] data) throws Exception {
        try {
            CuratorFrameworkInstance
            .getInstance()
            .create()
            .creatingParentsIfNeeded()
            .withMode(mode)
            .forPath(path, data);
        } catch (NodeExistsException e) {
            CuratorFrameworkInstance
            .getInstance()
            .setData()
            .forPath(path, data);
        }
    }

    /**
     * Table of contents is as follows:
     * 
     * /sysconfig
     * └── (JSON format system configuration) "{pingcycle: 60000}"
     * 
     * @return
     */
    public Sysconfig getSysconfig() throws Exception {
        return JsonConvert
                .getInstance()
                .fromJson(ByteDataConver
                        .getInstance()
                        .getConvertFromByteForString()
                        .getValue(CuratorFrameworkInstance
                                .getInstance()
                                .getData()
                                .forPath("/sysconfig")), Sysconfig.class);
    }
    public void setSysconfig(Sysconfig sysconfig) throws Exception {
        createOrSetData("/sysconfig", CreateMode.PERSISTENT,
                ByteDataConver
                .getInstance()
                .getConvertToByteForString()
                .getData(JsonConvert
                        .getInstance()
                        .tobeJson(sysconfig)));
    }

    /**
     * Table of contents is as follows:
     * 
     * /robot (Robot root directory)
     * ├── /templates (Robot plugin templates)
     * │   ├── /com_xxx_sss_PingCallTest
     * │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
     * │   ├── /com_xxx_sss_HttpCallTest
     * │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
     * │   ├── /com_xxx_sss_XXXXCallTest
     * │         └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
     * 
     * @return
     */
    private static TypeReference<Map<String, TemplateField>> TYP_PLUGTEMPLATE = new TypeReference<Map<String, TemplateField>>() {};
    public Map<String, TemplateField> getPlugTemplate(String plugName) throws Exception {
        String path = new StringBuilder()
                .append("/robot/templates/")
                .append(plugName).toString();
        return JsonConvert
                .getInstance()
                .fromJson(ByteDataConver
                        .getInstance()
                        .getConvertFromByteForString()
                        .getValue(CuratorFrameworkInstance
                                .getInstance()
                                .getData()
                                .forPath(path)), TYP_PLUGTEMPLATE);
    }
    public void setPlugTemplate(String plugName, Map<String, TemplateField> plugTemplate) throws Exception {
        String path = new StringBuilder()
                .append("/robot/templates/")
                .append(plugName).toString();
        createOrSetData(path, CreateMode.PERSISTENT,
                ByteDataConver
                .getInstance()
                .getConvertToByteForString()
                .getData(JsonConvert
                        .getInstance()
                        .tobeJson(plugTemplate)));
    }

    /**
     * Register a robot instance
     * 
     * /robot (Robot root directory)
     * ├── /register (Robot registration directory)
     * │   ├── /rebot-xxx (Robot group name)
     * │   │   ├──meta-info (Robot and task inf)
     * │   │   │   ├── /instance (Instance child nodes, all temporary nodes)
     * │   │   │   │   ├── /UUID01 ("ip@procid")
     * │   │   │   │   ├── /UUID02 ("ip@procid")
     * │   │   │   │   └── /UUID03 ("ip@procid")
     * 
     */
    private static final String REG_UUID = UUID.randomUUID().toString();
    public void regRobotInstance(String robotGropName) throws Exception {
        String path = new StringBuilder()
                .append("/register/")
                .append(robotGropName)
                .append("/meta-info/instance/")
                .append(REG_UUID).toString();
        String ipAddr = "none";
        try {
            InetAddress ip = InetAddress.getLocalHost();
            ipAddr = ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String instanceValue = new StringBuilder()
                .append(ipAddr)
                .append("@")
                .append(ProcessHandle.current().pid())
                .toString();
        createOrSetData(path, CreateMode.EPHEMERAL,
                ByteDataConver
                .getInstance()
                .getConvertToByteForString()
                .getData(instanceValue));
    }

    /**
     * Task config by taskName
     * @param taskName
     * @return
     * @throws Exception
     */
    public Properties getTaskConfigByTaskName(String taskName) throws Exception {
        String path = new StringBuilder()
                .append("/tasks/")
                .append(taskName)
                .append("/config").toString();
        return ByteDataConver
                .getInstance()
                .getConvertFromByteForProperties()
                .getValue(
                        CuratorFrameworkInstance
                        .getInstance()
                        .getData().forPath(path));
    }
    public void setTaskConfigByTaskName(String taskName, Properties properties) throws Exception {
        String path = new StringBuilder()
                .append("/tasks/")
                .append(taskName)
                .append("/config").toString();
        createOrSetData(path, CreateMode.PERSISTENT, 
                ByteDataConver
                .getInstance()
                .getConvertToByteForProperties()
                .getData(properties));
        
    }

    /**
     * Get plug name by taskName.
     * Just had get can not modify.
     * @param taskName
     * @return
     * @throws Exception
     */
    public String getPlugNameByTaskName(String taskName) throws Exception {
        String path = new StringBuilder()
                .append("/tasks/")
                .append(taskName).toString();
        return ByteDataConver
                .getInstance()
                .getConvertFromByteForString()
                .getValue(
                        CuratorFrameworkInstance
                        .getInstance()
                        .getData().forPath(path));
    }

    /**
     * Create one task
     * @param taskName
     * @param plugName
     * @param properties
     * @throws Exception
     */
    public void createTask(String taskName, String plugName, Properties properties) throws Exception {
        Map<String, TemplateField> plugTemplate =  getPlugTemplate(plugName);
        Set<String> plugFields = plugTemplate.keySet();
        for (String plugField : plugFields) {
            String plueFiledValue = properties.getProperty(plugField);
            if (ObjectUtils.isEmpty(plueFiledValue)) {
                throw new Exception(plugField + " is not config!");
            }
        }
        String taskPath = new StringBuilder()
                .append("/tasks/")
                .append(taskName).toString();

        CuratorFrameworkInstance
        .getInstance()
        .create()
        .creatingParentsIfNeeded()
        .withMode(CreateMode.PERSISTENT)
        .forPath(taskPath, ByteDataConver
                .getInstance()
                .getConvertToByteForString()
                .getData(plugName));
        try {
            String configPath = new StringBuilder()
                    .append("/tasks/")
                    .append(taskName)
                    .append("/config").toString();
            createOrSetData(configPath, CreateMode.PERSISTENT,
                    ByteDataConver
                    .getInstance()
                    .getConvertToByteForProperties()
                    .getData(properties));
        } catch (Exception e) {
            CuratorFrameworkInstance
            .getInstance()
            .delete()
            .forPath(taskPath);
            throw e;
        }
    }

    /**
     * 
     * ├── /register (Robot registration directory)
     * │   ├── /rebot-xxx (Robot group name)
     * │   │   ├──run-info (Running control info)
     * │   │   │   ├── /task-fire (Task trigger)
     * │   │   │   │   ├── /task-01
     * │   │   │   │   ├── /task-02
     * 
     * @param robotGropName
     * @throws Exception
     */
    public void taskFire(String robotGropName) throws Exception {
        String path = new StringBuilder()
                .append("/robot/register/")
                .append(robotGropName)
                .append("/meta-info/tasks")
                .toString();
        List<String> children = CuratorFrameworkInstance
                .getInstance()
                .getChildren()
                .forPath(path);
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        children.forEach((child) -> {
           try {
               String pathfire = new StringBuilder()
                       .append("/robot/register/")
                       .append(robotGropName)
                       .append("/run-info/task-fire/")
                       .append(child)
                       .toString();
               createOrSetData(pathfire, CreateMode.PERSISTENT, null);
           } catch (Exception e) {
               e.printStackTrace();
           }
        });
    }
}
