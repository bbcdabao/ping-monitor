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

package bbcdabao.pingmonitor.common.infra.coordination;

import java.util.UUID;

/**
/sysconfig
  └── (JSON format system configuration) "{pingcycle: 60000}"

/robot (Robot root directory)
├── /templates (Robot plugin templates)
│   ├── /com_xxx_sss_PingCallTest
│   │   └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
│   ├── /com_xxx_sss_HttpCallTest
│   │   └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, url: http://test.com}"
│   ├── /com_xxx_sss_XXXXCallTest
│   │   └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, calres: http://a.com}"
├── /register (Robot registration directory)
│   ├── /rebot-xxx (Robot group name)
│   │   ├── /meta-info (Robot and task inf)
│   │   │   ├── /instance (Instance child nodes, all EPHEMERAL nodes)
│   │   │   │   ├── /UUID01 ("ip@procid")
│   │   │   │   ├── /UUID02 ("ip@procid")
│   │   │   ├── /tasks (Monitoring task list)
│   │   │   │   ├── /task-01
│   │   │   │   └── /task-02
│   │   ├── /run-info (Runing controle info)
│   │   │   ├── /election (Robot instance election)
│   │   │   ├── /master-instance (EPHEMERAL nodes)
│   │   │   │   ├── /UUID01 ()
│   │   │   ├── /tasks (Assigned tasks)
│   │   │   │   ├── /UUID01
│   │   │   │   │   └── /Utask-02
│   │   │   │   ├── /UUID02 ()
│   │   │   │   │   └── /Utask-01
/tasks (Task configuration)
├── /task-01 (Robot plugin template: com_xxx_sss_PingCallTest)
│   └── /config (Properties format) "{ip=127.0.0.1, port=3251}"
├── /task-02 (Robot plugin template: com_xxx_sss_HttpCallTest)
│   └── /config (Properties format) "{url=https://baiduaa.com}"
│   
/result (Monitoring results, Use Zookeeper for small scale and Redis for large capacity)
├── /task-01
│   ├── /rebot-xxx (300ms)
│   └── /rebot-xxx (300ms)
├── /task-02
│   ├── /rebot-xxx (300ms)
│   └── /rebot-xxx (500ms)
 */

public interface IPath {

    String get();

    static IPath getPath(String path) {
        return () -> path;
    }

    /**
    /sysconfig
      └── (JSON format system configuration) "{pingcycle: 60000}"
     */
    static IPath sysconfig() {
        return () -> "/sysconfig";
    }

    /**
    /robot (Robot root directory)
    ├── /templates (Robot plugin templates)
    │   ├── /com_xxx_sss_PingCallTest
    │   │   └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
    │   ├── /com_xxx_sss_HttpCallTest
    │   │   └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, url: http://test.com}"
    │   ├── /com_xxx_sss_XXXXCallTest
    │   │   └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, calres: http://a.com}"
     */
    static IPath plugTemplate() {
        return () -> String.format("/robot/templates");
    }
    static IPath plugTemplate(String plugName) {
        return () -> String.format("/robot/templates/%s", plugName);
    }

    /**
     * Robot UUID
     */
    static String REG_UUID = UUID.randomUUID().toString();

    /**
    /robot (Robot root directory)
    ├── /register (Robot registration directory)
    │   ├── /rebot-xxx (Robot group name)
    │   │   ├── /meta-info (Robot and task inf)
    │   │   │   ├── /instance (Instance child nodes, all EPHEMERAL nodes)
    │   │   │   │   ├── /UUID01 ("ip@procid")
    │   │   │   │   ├── /UUID02 ("ip@procid")
     */
    static IPath robotRegisterPath() {
        return () -> "/robot/register";
    }
    static IPath robotRegisterPathGroup(String robotGroupName) {
    	return () -> String.format("/robot/register/%s", robotGroupName);
    }

    static IPath robotMetaInfoPath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/meta-info", robotGroupName);
    }

    static IPath robotMetaInfoInstancePath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/meta-info/instances", robotGroupName);
    }

    static IPath robotMetaInfoInstanceIdPath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/meta-info/instances/%s", robotGroupName, REG_UUID);
    }

    /**
    /robot (Robot root directory)
    ├── /register (Robot registration directory)
    │   ├── /rebot-xxx (Robot group name)
    │   │   ├── /meta-info (Robot and task inf)
    │   │   │   ├── /tasks (Monitoring task list)
    │   │   │   │   ├── /task-01
    │   │   │   │   └── /task-02
     */
    static IPath robotMetaInfoTaskPath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/meta-info/tasks", robotGroupName);
    }

    static IPath robotMetaInfoTaskPath(String robotGroupName, String taskName) {
        return () -> String.format("/robot/register/%s/meta-info/tasks/%s", robotGroupName, taskName);
    }

    /**
    /robot (Robot root directory)
    ├── /register (Robot registration directory)
    │   ├── /rebot-xxx (Robot group name)
    │   │   ├── /run-info (Runing controle info)
    │   │   │   ├── /election (Robot instance election)
    │   │   │   ├── /tasks (Assigned tasks)
    │   │   │   │   ├── /UUID01
    │   │   │   │   │   └── /Utask-02
    │   │   │   │   ├── /UUID02 ()
    │   │   │   │   │   └── /Utask-01
     */
    static IPath robotRunInfoElectionPath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/run-info/election", robotGroupName);
    }
    static IPath robotRunInfoMasterInstancePath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/run-info/master-instances", robotGroupName);
    }

    static IPath robotRunInfoMasterInstanceIdPath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/run-info/master-instances/%s", robotGroupName, REG_UUID);
    }
    static IPath robotRunInfoTaskPath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/run-info/task", robotGroupName);
    }
    static IPath robotRunInfoTaskPath(String robotGroupName, String robotUUID) {
        return () -> String.format("/robot/register/%s/run-info/task/%s", robotGroupName, robotUUID);
    }
    static IPath robotRunInfoTaskPath(String robotGroupName, String robotUUID, String taskName) {
        return () -> String.format("/robot/register/%s/run-info/task/%s/%s", robotGroupName, robotUUID, taskName);
    }

    /**
    /tasks (Task configuration)
    ├── /task-01 (Robot plugin template: com_xxx_sss_PingCallTest)
    │   └── /config (Properties format) "{ip=127.0.0.1, port=3251}"
    ├── /task-02 (Robot plugin template: com_xxx_sss_HttpCallTest)
    │   └── /config (Properties format) "{url=https://baiduaa.com}"
     */
    static IPath taskPath() {
        return () -> "/tasks";
    }
    static IPath taskPath(String taskName) {
        return () -> String.format("/tasks/%s", taskName);
    }
    static IPath taskConfigPath(String taskName) {
        return () -> String.format("/tasks/%s/config", taskName);
    }

    /**
    /result (Monitoring results)
    ├── /task-01
    │   ├── /rebot-xxx (300ms)
    │   └── /rebot-xxx (300ms)
    ├── /task-02
    │   ├── /rebot-xxx (300ms)
    │   └── /rebot-xxx (500ms)
     */
    static IPath resultPath() {
        return () -> "/result";
    }
    static IPath resultPath(String taskName) {
        return () -> String.format("/result/%s", taskName);
    }
    static IPath resultPath(String taskName, String robotGroupName) {
        return () -> String.format("/result/%s/%s", taskName, robotGroupName);
    }
}