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

package bbcdabao.pingmonitor.manager.app.services.impl;

import org.apache.zookeeper.KeeperException.NoNodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import bbcdabao.pingmonitor.common.domain.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.domain.coordination.Sysconfig;

@Service
public class SyncSysconfig implements ApplicationRunner {

    @Autowired
    private Sysconfig sysconfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (sysconfig.isOverwrite()) {
            CoordinationManager
            .getInstance()
            .setSysconfig(sysconfig);
            return;
        }
        try {
            CoordinationManager
            .getInstance()
            .getSysconfig();
        } catch (NoNodeException e) {
            CoordinationManager
            .getInstance()
            .setSysconfig(sysconfig);
        }        
    }
}
