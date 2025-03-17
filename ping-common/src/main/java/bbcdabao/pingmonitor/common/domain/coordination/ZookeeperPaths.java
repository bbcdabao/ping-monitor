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

package bbcdabao.pingmonitor.common.domain.coordination;

public class ZookeeperPaths {

    private static class Holder {
        private static final ZookeeperPaths INSTANCE = new ZookeeperPaths();
    }

    public static ZookeeperPaths getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * /sysconfig
     * └── (JSON format system configuration) "{pingcycle: 60000}"
     */
    public String getSysconfigPath() {
        return "/sysconfig";
    }

    /**
     * /robot (Robot root directory)
     * ├── /register (Robot registration directory)
     * │   ├── /rebot-xxx (Robot group name)
     * │   │   ├──meta-info (Robot and task inf)
     * │   │   │   ├── /instance (Instance child nodes, all temporary nodes)
     */
    public String getRobotInstancePath(String robotGropName) {
        return String.format("/robot/register/%s/meta-info/instance", robotGropName);
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
    public String getPlugTemplatePath(String plugName) {
        return String.format("/robot/templates/%s", plugName);
    }

    public String getTaskConfigPath(String taskName) {
        return String.format("/tasks/%s/config", taskName);
    }
}
