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
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs;

import com.fasterxml.jackson.core.type.TypeReference;

import bbcdabao.pingmonitor.common.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.extraction.TemplateField;
import bbcdabao.pingmonitor.common.json.JsonConvert;
import bbcdabao.pingmonitor.common.zkclientframe.core.CuratorFrameworkInstance;

/**
*
* Distributed coordination
* 
* /sysconfig
* └── (JSON format system configuration) "{pingcycle: 60000}"
* 
* /robot (Robot root directory)
* ├── /templates (Robot plugin templates)
* │   ├── /com_xxx_sss_PingCallTest
* │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
* │   ├── /com_xxx_sss_HttpCallTest
* │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
* │   ├── /com_xxx_sss_XXXXCallTest
* │         └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
* ├── /register (Robot registration directory)
* │   ├── /rebot-xxx (Robot group name)
* │   │   ├── /instance (Instance child nodes, all temporary nodes)
* │   │   │   ├── /UUID01 ("ip@procid")
* │   │   │   ├── /UUID02 ("ip@procid")
* │   │   │   └── /UUID03 ("ip@procid")
* │   │   ├── /tasks (Monitoring task list, child nodes must be unique)
* │   │   │   ├── /task-01 (Scheduling concurrency configuration)
* │   │   │   └── /task-02 (Scheduling concurrency configuration)
* 
* /tasks (Task configuration)
* ├── /task-01 (Robot plugin template: com_xxx_sss_PingCallTest)
* │   └── /config (Properties format) "{ip=127.0.0.1, port=3251}"
* ├── /task-02 (Robot plugin template: com_xxx_sss_HttpCallTest)
* │   └── /config (Properties format) "{url=https://baiduaa.com}"
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

    private void deleteAndcreateWithTtl(String path, byte[] data) throws Exception {
        try {
            CuratorFrameworkInstance.getInstance().getZookeeperClient().getZooKeeper().create(
                    path,
                    "Some data".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL,
                    null,
                    1000
            );

            CuratorFrameworkInstance
            .getInstance()
            .create()
            .creatingParentsIfNeeded()
            .withMode(CreateMode.EPHEMERAL)
            .withTtl()
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
        StringBuilder sb = new StringBuilder()
                .append("/robot/templates/")
                .append(plugName);
        return JsonConvert
                .getInstance()
                .fromJson(ByteDataConver
                        .getInstance()
                        .getConvertFromByteForString()
                        .getValue(CuratorFrameworkInstance
                                .getInstance()
                                .getData()
                                .forPath(sb.toString())), TYP_PLUGTEMPLATE);
    }

    public void putPlugTemplate(String plugName, Map<String, TemplateField> plugTemplate) throws Exception {
        StringBuilder sb = new StringBuilder()
                .append("/robot/templates/")
                .append(plugName);
        createOrSetData(sb.toString(), CreateMode.PERSISTENT,
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
     * ├── /register (Robot registration directory)
     * │   ├── /rebot-xxx (Robot group name)
     * │   │   ├── /instance (Instance child nodes, all temporary nodes)
     * │   │   │   ├── /UUID01 ("ip@procid")
     * │   │   │   ├── /UUID02 ("ip@procid")
     * │   │   │   └── /UUID03 ("ip@procid")
     * 
     */
    private static final String REG_UUID = UUID.randomUUID().toString();
    public void regRobotInstance(String robotGropName) throws Exception {
        StringBuilder sb = new StringBuilder()
                .append("/register/")
                .append(robotGropName)
                .append("/instance/")
                .append(REG_UUID);
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
        createOrSetData(sb.toString(), CreateMode.EPHEMERAL,
                ByteDataConver
                .getInstance()
                .getConvertToByteForString()
                .getData(instanceValue));
    }

    /**
     * Get task config by taskName
     * @param taskName
     * @return
     * @throws Exception
     */
    public Properties getTaskConfigByTaskName(String taskName) throws Exception {
        StringBuilder sb = new StringBuilder()
                .append("/tasks/")
                .append(taskName)
                .append("/config");
        return ByteDataConver
                .getInstance()
                .getConvertFromByteForProperties()
                .getValue(
                        CuratorFrameworkInstance
                        .getInstance()
                        .getData().forPath(sb.toString()));
    }

    /**
     * Get plug name by taskName
     * @param taskName
     * @return
     * @throws Exception
     */
    public String getPlugNameByTaskName(String taskName) throws Exception {
        StringBuilder sb = new StringBuilder()
                .append("/tasks/")
                .append(taskName);
        return ByteDataConver
                .getInstance()
                .getConvertFromByteForString()
                .getValue(
                        CuratorFrameworkInstance
                        .getInstance()
                        .getData().forPath(sb.toString()));
    }
}
