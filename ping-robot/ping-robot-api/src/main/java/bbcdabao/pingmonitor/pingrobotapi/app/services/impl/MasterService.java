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
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.CollectionUtils;

import bbcdabao.pingmonitor.common.domain.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.domain.coordination.IPath;
import bbcdabao.pingmonitor.common.domain.coordination.MasterKeeperTaskManager;
import bbcdabao.pingmonitor.common.domain.coordination.MasterRobotInfo;
import bbcdabao.pingmonitor.common.domain.coordination.Sysconfig;
import bbcdabao.pingmonitor.common.domain.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.domain.json.JsonConvert;
import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfig;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfigNotify;
import bbcdabao.pingmonitor.pingrobotapi.domain.RobotConfig;
import jakarta.annotation.PostConstruct;

public class MasterService extends TimeWorkerBase implements ApplicationRunner, ISysconfigNotify {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterService.class);

    /**
     * Monitor changes
     */
    private class MonitorChange extends BaseEventHandler {
        @Override
        public void onEvent(CreatedEvent data) throws Exception {
            tsAssign.set(0);
            LOGGER.info("MasterService.MonitorChange.CreatedEvent");
        }
        @Override
        public void onEvent(ChangedEvent data) throws Exception {
            tsAssign.set(0);
            LOGGER.info("MasterService.MonitorChange.ChangedEvent");
        }
        @Override
        public void onEvent(DeletedEvent data) throws Exception {
            tsAssign.set(0);
            LOGGER.info("MasterService.MonitorChange.DeletedEvent");
        }
    }

    @Autowired
    private ISysconfig regSysconfigNotify;

    @Autowired
    private RobotConfig robotConfig;
    
    private MonitorChange monitorChange = null;

    private AtomicLong tsAssign = new AtomicLong(0);
    
    private AtomicBoolean isMaster = new AtomicBoolean(false);

    private IPath robotMetaInfoTaskPath;
    private IPath robotRunInfoTaskPath;
    private IPath robotMetaInfoPath;
    private IPath robotRunInfoElectionPath;
    private IPath robotRunInfoMasterInstanceIdPath;

    private class NtaskInfo {
        private String nod;
        private long czxid;
        public NtaskInfo(String nod, long czxid) {
            this.nod = nod;
            this.czxid = czxid;
        }
    }

    private List<NtaskInfo> getNtasks() {
        CoordinationManager cm = CoordinationManager.getInstance();
        List<NtaskInfo> ntasks = new ArrayList<>();
        try {
            cm.getChildren(robotMetaInfoTaskPath,
                    (IPath childPath, String child, Stat stat) -> {
                ntasks.add(new NtaskInfo(child, stat.getCzxid()));
            });
        } catch (Exception e) {
            LOGGER.info("MasterService-getNtasks Exception:{}", e.getMessage());
        }
        return ntasks;
    }
    
    private List<String> getRobots() {
        CoordinationManager cm = CoordinationManager.getInstance();
        List<String> robots = null;
        try {
            robots = cm.getChildren(robotMetaInfoTaskPath);
        } catch (Exception e) {
            LOGGER.info("MasterService-getRobots Exception:{}", e.getMessage());
        }
        return robots;
    }

    private void assignTasks() throws Exception {
        if(0 != tsAssign.getAndIncrement()) {
            return;
        }
        LOGGER.info("MasterService-doReMake Enter");
        CoordinationManager cm = CoordinationManager.getInstance();

        cm.deleteData(robotRunInfoTaskPath);

        List<NtaskInfo> ntasks = getNtasks();
        if (CollectionUtils.isEmpty(ntasks)) {
            LOGGER.info("MasterService-doReMake ntasks is empty!");
            return;
        }

        List<String> robots = getRobots();
        if (CollectionUtils.isEmpty(robots)) {
            LOGGER.info("MasterService-doReMake robots is empty!");
            return;
        }

        int robotCount = robots.size();
        ntasks.forEach(ntask -> {
            int ntaskMod = (int)(ntask.czxid % robotCount);
            String robotUUID = robots.get(ntaskMod);
            try {
                cm.createOrSetData(IPath.robotRunInfoTaskPath(robotConfig.getRobotGroupName(),
                        robotUUID, ntask.nod), CreateMode.PERSISTENT, null);
                LOGGER.info("MasterService-doReMake task:{} to:{}", ntask.nod, robotUUID);
            } catch (Exception e) {
                LOGGER.info("MasterService-doReMake create job Exception:{}", e.getMessage());
            }
        });
        LOGGER.info("MasterService-doReMake Over");
    }

    @FunctionalInterface
    public interface ActionDo {
        void execute() throws Exception;
    }
    private void onMaster(ActionDo act) {
        MasterRobotInfo masterRobotInfo = new MasterRobotInfo();
        if (0 == tsAssign.get()) {
            masterRobotInfo.setAct(true);
            LOGGER.info("ffffffffffffffffffffffffffffffffffffffffff");
        } else {
            masterRobotInfo.setAct(false);
            LOGGER.info("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
        }
        masterRobotInfo.setInfo("success");
        long beg = System.currentTimeMillis();
        try {
            act.execute();
            masterRobotInfo.setLatency(System.currentTimeMillis() - beg);
        } catch (Exception e) {
            masterRobotInfo.setLatency(System.currentTimeMillis() - beg);
            masterRobotInfo.setInfo(e.getMessage());
            LOGGER.info("MasterService-onMaster Exception:{}", e.getMessage());
        }
        try {
            CoordinationManager
            .getInstance()
            .setOrCreateData(robotRunInfoMasterInstanceIdPath, CreateMode.EPHEMERAL,
                    ByteDataConver
                    .getInstance()
                    .getConvertToByteForString()
                    .getData(
                            JsonConvert
                            .getInstance()
                            .tobeJson(masterRobotInfo)));
        }  catch (Exception e) {
            LOGGER.info("MasterService-onMaster write info Exception:{}", e.getMessage());
        }
    }

    private void onSlave(ActionDo act) {
        try {
            act.execute();
        } catch (Exception e) {
            LOGGER.info("MasterService-onSlave Exception:{}", e.getMessage());
        }
    }

    @PostConstruct
    public void init() {
        regSysconfigNotify.reg(this);
        String robotGroupName = robotConfig.getRobotGroupName();
        robotMetaInfoTaskPath = IPath.robotMetaInfoTaskPath(robotGroupName);
        robotRunInfoTaskPath = IPath.robotRunInfoTaskPath(robotGroupName);
        robotMetaInfoPath = IPath.robotMetaInfoPath(robotGroupName);
        robotRunInfoElectionPath = IPath.robotRunInfoElectionPath(robotGroupName);
        robotRunInfoMasterInstanceIdPath = IPath.robotRunInfoMasterInstanceIdPath(robotGroupName);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        MasterKeeperTaskManager.getInstance().selectMasterNotify(robotRunInfoElectionPath.get(),
                () -> {
                    isMaster.set(true);
                    if (null == monitorChange) {
                        monitorChange = new MonitorChange();
                        monitorChange.start(robotMetaInfoPath.get());
                    }
                },
                () -> {
                    isMaster.set(false);
                    if (null != monitorChange) {
                        monitorChange.gameOver();
                        monitorChange = null;
                    }
                    tsAssign.set(0);
                }, null);
    }

    @Override
    public void onChange(Sysconfig config) throws Exception {
        beginCron(config.getCronMain());
    }

    @Override
    public void onExecute() throws Exception {
        if (isMaster.get()) {
            onMaster(() -> {
                assignTasks();
            });
        } else {
            final CoordinationManager cm = CoordinationManager.getInstance();
            onSlave(() -> {
                cm.checkExists(robotRunInfoMasterInstanceIdPath,
                        (Stat stat) -> {
                            cm.deleteData(robotRunInfoMasterInstanceIdPath);
                        }, null);
            });
        }
    }
}
