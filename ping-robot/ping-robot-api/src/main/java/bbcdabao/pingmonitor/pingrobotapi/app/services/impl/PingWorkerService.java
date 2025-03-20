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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import bbcdabao.pingmonitor.common.domain.FactoryBase;
import bbcdabao.pingmonitor.common.domain.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.domain.coordination.IPath;
import bbcdabao.pingmonitor.common.domain.coordination.Sysconfig;
import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfig;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfigNotify;
import bbcdabao.pingmonitor.pingrobotapi.domain.RobotConfig;
import jakarta.annotation.PostConstruct;

public class PingWorkerService extends TimeWorkerBase implements ApplicationRunner, ISysconfigNotify {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingWorkerService.class);

    private class MonitorChange extends BaseEventHandler {
        @Override
        public void onEvent(CreatedEvent data) throws Exception {
            isReMake.set(true);
        }
        @Override
        public void onEvent(ChangedEvent data) throws Exception {
            isReMake.set(true);
        }
        @Override
        public void onEvent(DeletedEvent data) throws Exception {
            isReMake.set(true);
        }
    }

    @Autowired
    private ISysconfig regSysconfigNotify;

    private MonitorChange monitorChange = new MonitorChange();
    private AtomicBoolean isReMake = new AtomicBoolean(true);

    private void doReMake() throws Exception {
        CoordinationManager cm = CoordinationManager.getInstance();
        String robotGroupName = FactoryBase
                .getFactory()
                .getBean(RobotConfig.class).getRobotGroupName();
        cm.getChildren(IPath.robotRunInfoTaskPath(robotGroupName, IPath.REG_UUID));
        
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
        monitorChange.start("NODE:" +
        IPath.robotRunInfoTaskPath(robotGroupName, IPath.REG_UUID).get());
    }

    @Override
    public void onExecute() throws Exception {
        if (isReMake.get()) {
            doReMake();
            isReMake.set(false);
        }
    }
}
