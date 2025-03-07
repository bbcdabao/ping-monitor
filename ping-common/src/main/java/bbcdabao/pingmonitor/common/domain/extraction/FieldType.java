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

package bbcdabao.pingmonitor.common.domain.extraction;

import java.util.HashMap;
import java.util.Map;

/**
 * Only support java type
 */
public enum FieldType {
    BYTE("BYTE") {
        @Override
        public Object getValueFromString(String value) {
            return Byte.parseByte(value);
        }
    },
    SHORT("SHORT") {
        @Override
        public Object getValueFromString(String value) {
            return Short.parseShort(value);
        }
    },
    BOOLEAN("BOOLEAN") {
        @Override
        public Object getValueFromString(String value) {
            return Boolean.parseBoolean(value);
        }
    },
    INT("INT") {
        @Override
        public Object getValueFromString(String value) {
            return Integer.parseInt(value);
        }
    },
    LONG("LONG") {
        @Override
        public Object getValueFromString(String value) {
            return Long.parseLong(value);
        }
    },
    FLOAT("FLOAT") {
        @Override
        public Object getValueFromString(String value) {
            return Float.parseFloat(value);
        }
    },
    DOUBLE("DOUBLE") {
        @Override
        public Object getValueFromString(String value) {
            return Double.parseDouble(value);
        }
    },
    STRING("STRING") {
        @Override
        public Object getValueFromString(String value) {
            return value;
        }
    };

    private static final Map<Class<?>, FieldType> TYPEMAP = new HashMap<>(16);
    static {
        TYPEMAP.put(Byte.class, FieldType.BYTE);
        TYPEMAP.put(byte.class, FieldType.BYTE);
        TYPEMAP.put(Short.class, FieldType.SHORT);
        TYPEMAP.put(short.class, FieldType.SHORT);
        TYPEMAP.put(boolean.class, FieldType.BOOLEAN);
        TYPEMAP.put(Boolean.class, FieldType.BOOLEAN);
        TYPEMAP.put(Integer.class, FieldType.INT);
        TYPEMAP.put(int.class, FieldType.INT);
        TYPEMAP.put(Integer.class, FieldType.INT);
        TYPEMAP.put(long.class, FieldType.LONG);
        TYPEMAP.put(Long.class, FieldType.LONG);
        TYPEMAP.put(float.class, FieldType.FLOAT);
        TYPEMAP.put(Float.class, FieldType.FLOAT);
        TYPEMAP.put(double.class, FieldType.DOUBLE);
        TYPEMAP.put(Double.class, FieldType.DOUBLE);
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

    public abstract Object getValueFromString(String value);
}
