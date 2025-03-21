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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import bbcdabao.pingmonitor.common.domain.PingmonitorExecutor;
import bbcdabao.pingmonitor.common.domain.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.domain.coordination.IPath;
import bbcdabao.pingmonitor.common.domain.coordination.Sysconfig;
import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfig;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfigNotify;
import bbcdabao.pingmonitor.pingrobotapi.domain.RobotConfig;
import jakarta.annotation.PostConstruct;

public class PingWorkerService extends TimeWorkerBase implements ApplicationRunner, ISysconfigNotify {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingWorkerService.class);

    /**
     * Monitor changes
     */
    private class MonitorChange extends BaseEventHandler {
        @Override
        public void onEvent(CreatedEvent data) throws Exception {
            isReMake.set(true);
            LOGGER.info("PingWorkerService.MonitorChange.CreatedEvent");
        }
        @Override
        public void onEvent(ChangedEvent data) throws Exception {
            isReMake.set(true);
            LOGGER.info("PingWorkerService.MonitorChange.ChangedEvent");
        }
        @Override
        public void onEvent(DeletedEvent data) throws Exception {
            isReMake.set(true);
            LOGGER.info("PingWorkerService.MonitorChange.DeletedEvent");
        }
    }

    @Autowired
    private ISysconfig regSysconfigNotify;

    @Autowired
    private RobotConfig robotConfig;

    private MonitorChange monitorChange = new MonitorChange();

    private AtomicBoolean isReMake = new AtomicBoolean(true);

    private Map<String, PlugInfo> plugInfoMap = new HashMap<>(100);

    private IPath robotRunInfoTaskPath;
    private String pathMonitor;

    private class PlugInfo {
        private String taskName;
        /**
         * system update
         */
        private long sytime;
        /**
         * task config update
         */
        private long mtime;
        private IPingMoniterPlug plug;
    }

    private void checkRemove() {
        CoordinationManager cm = CoordinationManager.getInstance();
        Iterator<Map.Entry<String, PlugInfo>> iterator = plugInfoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PlugInfo> entry = iterator.next();
            PlugInfo plugInfo = entry.getValue();
            try {
                Stat stat = cm.getStat(IPath.taskConfigPath(plugInfo.taskName));
                if (stat.getMtime() != plugInfo.mtime) {
                    iterator.remove();
                }
            } catch (Exception e) {
                LOGGER.info("PingWorkerService-checkRemove Exception:{}", e.getMessage());
            }
        }
    }

    private List<String> rtasks = new ArrayList<>();
    private void doReMake() {
        LOGGER.info("PingWorkerService-doReMake Enter");
        CoordinationManager cm = CoordinationManager.getInstance();
        try {
            rtasks = cm.getChildren(robotRunInfoTaskPath);
        } catch (Exception e) {
            LOGGER.info("PingWorkerService-doReMake Exception:{}", e.getMessage());
        }
    }

    @PostConstruct
    public void init() {
        regSysconfigNotify.reg(this);
        robotRunInfoTaskPath = IPath.robotRunInfoTaskPath(robotConfig.getRobotGroupName(), IPath.REG_UUID);
        pathMonitor = "NODE:" + robotRunInfoTaskPath.get();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        monitorChange.start(pathMonitor);
    }

    @Override
    public void onChange(Sysconfig config) throws Exception {
        beginCron(config.getCronTask());
    }

    @Override
    public void onExecute() throws Exception {
        if (isReMake.get()) {
            doReMake();
            isReMake.set(false);
        }
        PingmonitorExecutor.getInstance().execute(() -> {
            
        });
    }
}
