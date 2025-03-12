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

package bbcdabao.pingmonitor.common.domain;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Global object factory
 */
public abstract class FactoryBase {
    private static AtomicReference<FactoryBase> INSTANCE = new AtomicReference<>();
    public static void setInstance(FactoryBase factory) {
        if (!INSTANCE.compareAndSet(null, factory)) {
            throw new IllegalStateException("FactoryBase allready set!");
        }
    }
    public static FactoryBase getFactory() {
        FactoryBase factory = INSTANCE.get();
        if (factory == null) {
            throw new IllegalStateException("FactoryBase not set!");
        }
        return factory;
    }

    abstract <T> T getBean(Class<T> clazz);
}
