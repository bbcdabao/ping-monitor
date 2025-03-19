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

import java.util.UUID;

/**
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
 * │   │   │   ├── /sub-tasks (allocation tasks)
 * │   │   │   │   ├── /UUID01 ("ip@procid")
 * │   │   │   │   ├── /UUID02 ("ip@procid")
 * │   │   │   │   └── /UUID03 ("ip@procid")
 * │   │   │   ├── /tasks (Monitoring task list, child nodes must be unique)
 * │   │   │   │   ├── /task-01 (Scheduling concurrency configuration)
 * │   │   │   │   └── /task-02 (Scheduling concurrency configuration)
 * │   │   ├──run-info (Running control info)
 * │   │   │   └── /election (Robot instance election)
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
 */

public interface IPath {

    String get();

    static IPath getPath(String path) {
        return () -> path;
    }

    /**
     * /sysconfig
     * └── (JSON format system configuration) "{pingcycle: 60000}"
     */
    static IPath sysconfig() {
        return () -> "/sysconfig";
    }

    /**
     * /robot (Robot root directory)
     * ├── /templates (Robot plugin templates)
     * │   ├── /com_xxx_sss_PingCallTest
     * │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
     * │   ├── /com_xxx_sss_HttpCallTest
     * │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
     * │   ├── /com_xxx_sss_XXXXCallTest
     * │         └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
     */
    static IPath plugTemplate(String plugName) {
        return () -> String.format("/robot/templates/%s", plugName);
    }

    /**
     * /robot (Robot root directory)
     * ├── /register (Robot registration directory)
     * │   ├── /rebot-xxx (Robot group name)
     * │   │   ├──meta-info (Robot and task inf)
     * │   │   │   ├── /instances (Instance child nodes, all temporary nodes)
     * │   │   │   │   ├── /UUID01 ("ip@procid")
     * │   │   │   │   ├── /UUID02 ("ip@procid")
     * │   │   │   ├── /sub-tasks (allocation tasks)
     * │   │   │   │   ├── /UUID01
     * │   │   │   │   │   └── /task-02
     * │   │   │   │   ├── /UUID02
     * │   │   │   │   │   └── /task-01
     * │   │   │   ├── /tasks (Monitoring task list, child nodes must be unique)
     * │   │   │   │   ├── /task-01 (Scheduling concurrency configuration)
     * │   │   │   │   └── /task-02 (Scheduling concurrency configuration)
     */
    static IPath robotInstancePath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/meta-info/instances", robotGroupName);
    }

    static String REG_UUID = UUID.randomUUID().toString();
    static IPath robotInstanceIdPath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/meta-info/instances/%s", robotGroupName, REG_UUID);
    }

    static IPath robotTaskPath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/meta-info/tasks", robotGroupName);
    }

    static IPath robotTaskPath(String robotGroupName, String taskName) {
        return () -> String.format("/robot/register/%s/meta-info/tasks/%s", robotGroupName, taskName);
    }

    static IPath robotSubTaskPath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/meta-info/sub-tasks", robotGroupName);
    }

    static IPath robotSubTaskPath(String robotGroupName, String robotId, String taskName) {
        return () -> String.format("/robot/register/%s/meta-info/sub-tasks/%s/%s", robotGroupName, robotId, taskName);
    }

    static IPath robotSubTaskIdPath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/meta-info/sub-tasks/%s", robotGroupName, REG_UUID);
    }

    /**
     * /robot (Robot root directory)
     * ├── /register (Robot registration directory)
     * │   ├── /rebot-xxx (Robot group name)
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
     */
    static IPath robotElectionPath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/run-info/election", robotGroupName);
    }
    static IPath robotTaskFirePath(String robotGroupName) {
        return () -> String.format("/robot/register/%s/run-info/task-fire", robotGroupName);
    }
    static IPath robotTaskFirePath(String robotGroupName, String taskName) {
        return () -> String.format("/robot/register/%s/run-info/task-fire/%s", robotGroupName, taskName);
    }

    /**
     * /tasks (Task configuration)
     * ├── /task-01 (Robot plugin template: com_xxx_sss_PingCallTest)
     * │   └── /config (Properties format) "{ip=127.0.0.1, port=3251}"
     * ├── /task-02 (Robot plugin template: com_xxx_sss_HttpCallTest)
     * │   └── /config (Properties format) "{url=[https://baiduaa.com}](https://baiduaa.com})"
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
}
