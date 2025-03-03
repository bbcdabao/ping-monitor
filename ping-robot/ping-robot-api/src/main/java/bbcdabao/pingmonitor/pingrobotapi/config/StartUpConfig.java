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

package bbcdabao.pingmonitor.pingrobotapi.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;

import bbcdabao.pingmonitor.common.constants.PatchConstant;
import bbcdabao.pingmonitor.common.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.extraction.ExtractionField;
import bbcdabao.pingmonitor.common.extraction.TemplateField;
import bbcdabao.pingmonitor.common.json.JsonConvert;
import bbcdabao.pingmonitor.common.zkclientframe.core.CuratorFrameworkInstance;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;
import bbcdabao.pingmonitor.pingrobotapi.constants.RobotConstant;
import bbcdabao.pingmonitor.pingrobotapi.templates.TemplatesManager;
import lombok.Data;

@ConfigurationProperties(prefix = "robot")
@Data
public class StartUpConfig implements ApplicationRunner {

    private String robotGroupName;

    private void pushTemplatesInfo() throws Exception {
        TemplatesManager.getInstance().checkPingMoniterPlug((plugName, plugClazz) -> {
            IPingMoniterPlug plug = plugClazz.getConstructor().newInstance();
            Map<String, TemplateField> plugTemplate = ExtractionField.getInstance()
                    .extractionTemplateMapFromObject(plug);
            String strPlugTemplate = JsonConvert.getInstance().tobeJson(plugTemplate);
            byte[] bytePlugTemplate = ByteDataConver.getInstance().getConvertToByteForString().getData(strPlugTemplate);
            String plugNamePath = new StringBuilder().append(PatchConstant.ROBOT_TEMPLATES).append("/").append(plugName)
                    .toString();
            try {
                CuratorFrameworkInstance.getInstance().create().creatingParentsIfNeeded().forPath(plugNamePath,
                        bytePlugTemplate);
            } catch (NodeExistsException e) {
                CuratorFrameworkInstance.getInstance().setData().forPath(plugNamePath, bytePlugTemplate);
            }
        });
    }

    private void regInstance() throws Exception {
        String instancePath = new StringBuilder().append(PatchConstant.ROBOT_REGISTERS).append("/")
                .append(robotGroupName).append("/").append(RobotConstant.ROBOT_ID).toString();
        String ipAddr = "none";
        try {
            InetAddress ip = InetAddress.getLocalHost();
            ipAddr = ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String instanceValue = new StringBuilder().append(ipAddr).append("@").append(ProcessHandle.current().pid())
                .toString();
        byte[] byteInstanceValue = ByteDataConver.getInstance().getConvertToByteForString().getData(instanceValue);

        try {
            CuratorFrameworkInstance.getInstance().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                    .forPath(instancePath, byteInstanceValue);
        } catch (NodeExistsException e) {
            CuratorFrameworkInstance.getInstance().setData().forPath(instancePath, byteInstanceValue);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        pushTemplatesInfo();
        regInstance();
    }

    private static AtomicReference<StartUpConfig> INSTANCE_REF = new AtomicReference<>();

    public static StartUpConfig getInstance() {
        return INSTANCE_REF.get();
    }
}
