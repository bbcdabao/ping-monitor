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

package bbcdabao.pingmonitor.manager.app.services;

import java.util.Collection;

import bbcdabao.pingmonitor.manager.app.module.responses.RobotGroupInfo;
import bbcdabao.pingmonitor.manager.app.module.responses.RobotInstanceInfo;

/**
 * 哨兵相关接口
 */
public interface IRobotManager {

    /**
     * 获取哨兵组
     * @param robotGroupName
     * @return
     * @throws Exception
     */
    Collection<RobotGroupInfo> getRobotGroupInfos(String robotGroupName) throws Exception;

    /**
     * 获取哨兵组实例信息
     * @param robotGroupName
     * @return
     * @throws Exception
     */
    Collection<RobotInstanceInfo> getRobotInstanceInfos(String robotGroupName) throws Exception;

    /**
     * 获取哨兵组任务列表
     * @param robotGroupName
     * @return
     * @throws Exception
     */
    Collection<String> getRobotGroupTasks(String robotGroupName) throws Exception;
}