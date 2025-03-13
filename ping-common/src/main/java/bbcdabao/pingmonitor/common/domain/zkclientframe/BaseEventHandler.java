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

package bbcdabao.pingmonitor.common.domain.zkclientframe;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.curator.framework.recipes.cache.CuratorCache;

import bbcdabao.pingmonitor.common.domain.zkclientframe.core.EventHandlerRegister;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;

/**
 * Zookeeper monitoring processing, used for shared monitoring
 */
public abstract class BaseEventHandler extends GameOver {

    /**
     * Monitoring interface
     */
    public static interface IRegister {
        CuratorCache reg(String patch, BaseEventHandler handler);
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
    public CuratorCache start(String patch) {
        return EventHandlerRegister.getInstance().reg(patch, this);
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
