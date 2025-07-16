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

package bbcdabao.pingmonitor.common.infra.zkclientframe;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.curator.framework.recipes.cache.CuratorCache;

import bbcdabao.pingmonitor.common.infra.zkclientframe.core.EventHandlerRegister;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.infra.zkclientframe.event.DeletedEvent;

/**
 * Zookeeper monitoring processing, used for shared monitoring
 * path format :
 * (1)  format like  scope:path eg.  ALL:/test 
 * (2)  ALL\CHILD\CHANGED\CREATE\DELETE\CREATEADNCHANGED\NODE
 */
public abstract class BaseEventHandler extends GameOver {
    private static AtomicLong INDEX = new AtomicLong(0L);
    /**
     * Monitoring interface
     */
    public static interface IRegister {
        CuratorCache reg(String patch, BaseEventHandler handler);
        void regAndBlokingRun(String patch, BaseEventHandler handler);
    }

    /**
     * Used in unique identification systems
     */
    private static AtomicLong CODE_INDEX = new AtomicLong(0);
    private final long code = CODE_INDEX.incrementAndGet();
    public long getCode() {
        return code;
    }

    /**
     * Call to start monitoring the path.
     * You can call it multiple times to monitor unreachable paths.
     * @param patch
     * @param register
     */
    public CuratorCache start(String path) {
        return EventHandlerRegister.getInstance().reg(path, this);
    }
    public CuratorCache startAlone(String path) {
        String pathNow = new StringBuilder()
                .append(path)
                .append("@")
                .append(INDEX.incrementAndGet())
                .toString();
        return EventHandlerRegister.getInstance().reg(pathNow, this);
    }
    public void startBloking(String path) {
        EventHandlerRegister.getInstance().regAndBlokingRun(path, this);
    }
    public void startBlokingAlone(String path) {
        String pathNow = new StringBuilder()
                .append(path)
                .append("@")
                .append(INDEX.incrementAndGet())
                .toString();
        EventHandlerRegister.getInstance().regAndBlokingRun(pathNow, this);
    }

    public void onEvent(CreatedEvent data) throws Exception {
    }
    public void onEvent(ChangedEvent data) throws Exception {
    }
    public void onEvent(DeletedEvent data) throws Exception {
    }
    public void onIdl() throws Exception {
    }
}