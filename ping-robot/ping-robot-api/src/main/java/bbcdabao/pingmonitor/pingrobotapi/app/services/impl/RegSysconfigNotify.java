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

package bbcdabao.pingmonitor.pingrobotapi.app.services.impl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.common.infra.coordination.Sysconfig;
import bbcdabao.pingmonitor.common.infra.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.infra.json.JsonConvert;
import bbcdabao.pingmonitor.common.infra.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfig;
import bbcdabao.pingmonitor.pingrobotapi.app.services.ISysconfigNotify;

public class RegSysconfigNotify extends BaseEventHandler implements ISysconfig, ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegSysconfigNotify.class);

    private final Collection<WeakReference<ISysconfigNotify>> sysconfigNotifys = new ArrayList<>();

    private AtomicReference<Sysconfig> sysconfigRef = new AtomicReference<>(null);

    private synchronized void onNotifySyn(Sysconfig sysconfig) {
        Iterator<WeakReference<ISysconfigNotify>> iterator = sysconfigNotifys.iterator();
        while (iterator.hasNext()) {
            ISysconfigNotify sysconfigNotify = iterator.next().get();
            if (sysconfigNotify != null) {
                try {
                    sysconfigNotify.onChange(sysconfig);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                iterator.remove();
            }
        }
    }
    private synchronized void sysconfigNotify(ISysconfigNotify notify) {
        WeakReference<ISysconfigNotify> weakNotify = new WeakReference<>(notify);
        sysconfigNotifys.add(weakNotify);
    }
    @Override
    public void reg(ISysconfigNotify notify) {
        sysconfigNotify(notify);
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        start(IPath.sysconfig().get());
    }
    @Override
    public void onEvent(CreatedEvent data) throws Exception {
        LOGGER.info("CreatedEvent:{}", data.getData().getPath());
        String strSysconfig = ByteDataConver
                .getInstance()
                .getConvertFromByteForString()
                .getValue(data.getData().getData());
        Sysconfig sysconfigNow = JsonConvert
                .getInstance()
                .fromJson(strSysconfig, Sysconfig.class);
        sysconfigRef.set(sysconfigNow);
        onNotifySyn(sysconfigNow);
    }
    @Override
    public void onEvent(ChangedEvent data) throws Exception {
        LOGGER.info("ChangedEvent:{}", data.getData().getPath());
        String strSysconfig = ByteDataConver
                .getInstance()
                .getConvertFromByteForString()
                .getValue(data.getData().getData());
        Sysconfig sysconfigNow = JsonConvert
                .getInstance()
                .fromJson(strSysconfig, Sysconfig.class);
        sysconfigRef.set(sysconfigNow);
        onNotifySyn(sysconfigNow);
    }
    @Override
    public void onEvent(DeletedEvent data) throws Exception {
        LOGGER.info("DeletedEvent:{}", data.getData().getPath());
    }
    @Override
    public Sysconfig getSysconfig() {
        return sysconfigRef.get();
    }
}