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

package bbcdabao.pingmonitor.pingrobotapi.app.services.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import bbcdabao.pingmonitor.common.domain.FactoryBase;
import bbcdabao.pingmonitor.common.domain.coordination.MasterKeeperTaskManager;
import bbcdabao.pingmonitor.common.domain.coordination.Sysconfig;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfig;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfigNotify;
import bbcdabao.pingmonitor.pingrobotapi.domain.RobotConfig;
import jakarta.annotation.PostConstruct;

public class MasterService extends TimeWorkerBase implements ApplicationRunner, ISysconfigNotify {

    @Autowired
    private ISysconfig regSysconfigNotify;

    private AtomicBoolean isMaster = new AtomicBoolean(false);

    private void doExecute() throws Exception {
        
    }

    @PostConstruct
    public void init() {
        regSysconfigNotify.reg(this);
    }

    @Override
    public void onChange(Sysconfig config) throws Exception {
        beginCron(config.getCron());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String robotGroupName = FactoryBase
                .getFactory()
                .getBean(RobotConfig.class).getRobotGroupName();
        String patch = new StringBuilder()
                .append("/robot/register/")
                .append(robotGroupName)
                .append("/run-info/election")
                .toString();
        MasterKeeperTaskManager.getInstance().selectMasterNotify(patch,
                () -> {
                    isMaster.set(true);
                },
                () -> {
                    isMaster.set(false);
                }, null);
    }

    @Override
    public void onExecute() throws Exception {
        if (isMaster.get()) {
            doExecute();
        }
    }
}
