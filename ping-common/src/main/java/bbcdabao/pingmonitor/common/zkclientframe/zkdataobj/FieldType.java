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

package bbcdabao.pingmonitor.common.zkclientframe.zkdataobj;

import java.util.HashMap;
import java.util.Map;

/**
 * Only support java type int \ long \ String \ boolean
 */
public enum FieldType {
    INT("INT"), LONG("LONG"), STRING("STRING"), BOOLEAN("BOOLEAN");

    private static final Map<Class<?>, FieldType> TYPEMAP = new HashMap<>();
    static {
        TYPEMAP.put(int.class, FieldType.INT);
        TYPEMAP.put(Integer.class, FieldType.INT);
        TYPEMAP.put(long.class, FieldType.LONG);
        TYPEMAP.put(Long.class, FieldType.LONG);
        TYPEMAP.put(boolean.class, FieldType.BOOLEAN);
        TYPEMAP.put(Boolean.class, FieldType.BOOLEAN);
        TYPEMAP.put(String.class, FieldType.STRING);
    }
    public static FieldType getType(Class<?> clazz) {
        return TYPEMAP.get(clazz);
    }

    private String info;

    private FieldType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
