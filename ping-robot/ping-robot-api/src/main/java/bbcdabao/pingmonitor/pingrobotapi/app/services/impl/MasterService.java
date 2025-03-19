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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.CollectionUtils;

import bbcdabao.pingmonitor.common.domain.FactoryBase;
import bbcdabao.pingmonitor.common.domain.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.domain.coordination.IPath;
import bbcdabao.pingmonitor.common.domain.coordination.MasterKeeperTaskManager;
import bbcdabao.pingmonitor.common.domain.coordination.Sysconfig;
import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfig;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfigNotify;
import bbcdabao.pingmonitor.pingrobotapi.domain.RobotConfig;
import jakarta.annotation.PostConstruct;

public class MasterService extends TimeWorkerBase implements ApplicationRunner, ISysconfigNotify {

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
    
    private AtomicBoolean isMaster = new AtomicBoolean(false);

    private String robotGroupName;

    private class NtaskInfo {
        private IPath path;
        private String nod;
        private long czxid;
        public NtaskInfo(IPath path, String nod, long czxid) {
            this.path = path;
            this.nod = nod;
            this.czxid = czxid;
        }
    }

    private void doReMake() throws Exception {
        CoordinationManager cm = CoordinationManager.getInstance();
        List<NtaskInfo> ntasks = new ArrayList<>();
        cm.getChildren(IPath.robotTask(robotGroupName), (IPath childPath, String child, Stat stat) -> {
            try {
                cm.putData(childPath, CreateMode.PERSISTENT, null);
                ntasks.add(new NtaskInfo(childPath, child, stat.getCzxid()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        cm.deleteData(IPath.robotSubTaskIdPath(robotGroupName));

        List<String> robots = cm.getChildren(IPath.robotInstancePath(robotGroupName));
        if (CollectionUtils.isEmpty(robots)) {
            return;
        }
        int robotCount = robots.size();
        ntasks.forEach(ntask -> {
            int ntaskMod = (int)(ntask.czxid % robotCount);
            String robotId = robots.get(ntaskMod);
            try {
                cm.putData(IPath.robotSubTaskPath(robotGroupName, robotId, ntask.nod), CreateMode.PERSISTENT, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void doExecute() throws Exception {
        if (isReMake.get()) {
            doReMake();
            isReMake.set(false);
        }
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
                    monitorChange.start(IPath.robotInstancePath(robotGroupName).get());
                    monitorChange.start(IPath.robotTask(robotGroupName).get());
                },
                () -> {
                    isMaster.set(false);
                    monitorChange.gameOver();
                }, null);
    }

    @Override
    public void onExecute() throws Exception {
        if (isMaster.get()) {
            doExecute();
        } else {
            isReMake.set(true);
        }
    }
}
