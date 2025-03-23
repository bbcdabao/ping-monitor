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
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.CollectionUtils;

import bbcdabao.pingmonitor.common.domain.PingmonitorExecutor;
import bbcdabao.pingmonitor.common.domain.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.domain.coordination.IPath;
import bbcdabao.pingmonitor.common.domain.coordination.Pingresult;
import bbcdabao.pingmonitor.common.domain.coordination.Sysconfig;
import bbcdabao.pingmonitor.common.domain.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.domain.json.JsonConvert;
import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfig;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfigNotify;
import bbcdabao.pingmonitor.pingrobotapi.domain.RobotConfig;
import bbcdabao.pingmonitor.pingrobotapi.domain.templates.TemplatesManager;
import jakarta.annotation.PostConstruct;

public class PingWorkerService extends TimeWorkerBase implements ApplicationRunner, ISysconfigNotify {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingWorkerService.class);

    /**
     * Monitor changes
     */
    private class MonitorChange extends BaseEventHandler {
        @Override
        public void onEvent(CreatedEvent data) throws Exception {
            tsAssign.set(0);
            LOGGER.info("PingWorkerService.MonitorChange.CreatedEvent");
        }
        @Override
        public void onEvent(ChangedEvent data) throws Exception {
            tsAssign.set(0);
            LOGGER.info("PingWorkerService.MonitorChange.ChangedEvent");
        }
        @Override
        public void onEvent(DeletedEvent data) throws Exception {
            tsAssign.set(0);
            LOGGER.info("PingWorkerService.MonitorChange.DeletedEvent");
        }
    }

    @Autowired
    private ISysconfig regSysconfigNotify;

    @Autowired
    private RobotConfig robotConfig;

    private MonitorChange monitorChange = new MonitorChange();

    private AtomicLong tsAssign = new AtomicLong(0);
    
    private int timeOutMs = 0;

    private Map<String, PlugInfo> plugInfoMap = new HashMap<>(100);

    private IPath robotRunInfoTaskPath;
    private String pathMonitor;

    private class PlugInfo {
        /**
         * system update
         */
        private long stime = 0;
        /**
         * task config update
         */
        private long mtime = 0;
        private IPingMoniterPlug plug = null;
    }


    private List<String> rtasks = new ArrayList<>();
    private long stime = System.currentTimeMillis();
    private void assignTasks() throws Exception {
        if(0 != tsAssign.getAndIncrement()) {
            return;
        }
        stime = System.currentTimeMillis();
        LOGGER.info("PingWorkerService-doReMake Enter");
        CoordinationManager cm = CoordinationManager.getInstance();
        try {
            rtasks = cm.getChildren(robotRunInfoTaskPath);
        } catch (Exception e) {
            LOGGER.info("PingWorkerService-assignTasks Exception:{}", e.getMessage());
        }
        if (CollectionUtils.isEmpty(rtasks)) {
            return;
        }
        rtasks.forEach(rtask -> {
            plugInfoMap.computeIfAbsent(rtask, key -> {
                return new PlugInfo();
            }).stime = stime;
        });
    }
    private void assignUpdate() throws Exception {
        CoordinationManager cm = CoordinationManager.getInstance();
        Iterator<Map.Entry<String, PlugInfo>> iterator = plugInfoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PlugInfo> entry = iterator.next();
            String taskName = entry.getKey();
            PlugInfo plugInfo = entry.getValue();
            try {
                if (plugInfo.stime != stime) {
                    iterator.remove();
                    continue;
                }
                if (null == plugInfo.plug) {
                    try {
                        Stat stat = new Stat();
                        plugInfo.plug = TemplatesManager.getInstance().getPingMoniterPlugUsedTaskName(taskName, stat);
                        plugInfo.mtime = stat.getMtime();
                    } catch (Exception e) {
                        LOGGER.info("PingWorkerService-assignUpdate.create task Exception:{}", e.getMessage());
                    }
                    continue;
                }
                Stat statGet = cm.getStat(IPath.taskConfigPath(taskName));
                if (statGet.getMtime() != plugInfo.mtime) {
                    try {
                        plugInfo.plug = TemplatesManager.getInstance().getPingMoniterPlugUsedTaskName(taskName);
                        plugInfo.mtime = statGet.getMtime();
                    } catch (Exception e) {
                        LOGGER.info("PingWorkerService-assignUpdate.update task Exception:{}", e.getMessage());
                    }
                }
            } catch (Exception e) {
                LOGGER.info("PingWorkerService-assignUpdate Exception:{}", e.getMessage());
            }
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
        timeOutMs = config.getTimeOutMs();
    }

    private void doPing(String taskName, PlugInfo plugInfo) {
        IPingMoniterPlug plug = plugInfo.plug;
        if (null == plug) {
            return;
        }
        Pingresult pingresult = new Pingresult();
        long beg = System.currentTimeMillis();
        try {
            plug.doPingExecute(timeOutMs);
            
            long sub = System.currentTimeMillis() - beg;
            pingresult.setDelay(sub);
            pingresult.setSuccess(true);
            pingresult.setInfo("success");
            
        } catch (Exception e) {
            String info = e.getMessage();
            
            long sub = System.currentTimeMillis() - beg;
            pingresult.setDelay(sub);
            pingresult.setSuccess(false);
            pingresult.setInfo(info);
            
            LOGGER.info("PingWorkerService-doPing Exception:{}", info);
        }
        try {
            CoordinationManager
            .getInstance()
            .setOrCreateData(IPath.resultPath(taskName,
                    robotConfig.getRobotGroupName()),
                    CreateMode.PERSISTENT,
                    ByteDataConver
                    .getInstance()
                    .getConvertToByteForString()
                    .getData(JsonConvert
                            .getInstance()
                            .tobeJson(pingresult)));
        } catch (Exception e) {
            LOGGER.info("PingWorkerService-write resultException:{}", e.getMessage());
        }
    }

    @Override
    public void onExecute() throws Exception {
        assignTasks();
        assignUpdate();
        plugInfoMap.forEach((taskName, plugInfo) -> {
            PingmonitorExecutor.getInstance().execute(() -> {
                doPing(taskName, plugInfo);
            });
        });
    }
}
