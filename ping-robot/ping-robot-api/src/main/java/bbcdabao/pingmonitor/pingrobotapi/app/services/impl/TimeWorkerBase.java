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

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import bbcdabao.pingmonitor.pingrobotapi.app.services.ITimeWorker;

public abstract class TimeWorkerBase implements ITimeWorker {

    private final Logger logger = LoggerFactory.getLogger(TimeWorkerBase.class);

    @Autowired
    private TaskScheduler schedulerTimeJob;
    private ScheduledFuture<?> timedFuture;

    private void stopOverInner() {
        if (timedFuture == null) {
            return;
        }
        timedFuture.cancel(false);
        timedFuture = null;
    }

    @Override
    public synchronized void beginFixedDelayTask(int intervalMs) {
        stopOverInner();
        timedFuture = schedulerTimeJob.scheduleWithFixedDelay(() -> {
            try {
                onExecute();
            }
            catch (Exception e) {
                logger.error("TimeWorkerBase.onExecute Exception:{}", e.getMessage());
            }
        }, Duration.ofMillis(intervalMs));
    }
    @Override
    public synchronized void beginFixedRateTask(int intervalMs) {
        stopOverInner();
        timedFuture = schedulerTimeJob.scheduleAtFixedRate(() -> {
            try {
                onExecute();
            }
            catch (Exception e) {
                logger.error("TimeWorkerBase.onExecute Exception:{}", e.getMessage());
            }
        }, Duration.ofMillis(intervalMs));
    }
    @Override
    public synchronized void beginCron(String cron) {
        stopOverInner();
        timedFuture = schedulerTimeJob.schedule(() -> {
            try {
                onExecute();
            }
            catch (Exception e) {
                logger.error("TimeWorkerBase.onExecute Exception:{}", e.getMessage());
            }
        }, new CronTrigger(cron));
    }
    @Override
    public synchronized void stopOver() {
        stopOverInner();
    }

    abstract void onExecute() throws Exception;
}
