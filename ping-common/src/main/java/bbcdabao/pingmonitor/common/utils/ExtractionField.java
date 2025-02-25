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

package bbcdabao.pingmonitor.common.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import bbcdabao.pingmonitor.common.zkclientframe.core.ZookeeperFieldExtraction.FieldType;
import lombok.Data;

/**
 * Extraction field that used ZookeeperField labeled.
 */
public class ExtractionField {

    /**
     * Used for annotation, fields that need to be turned into property
     * configurations and saved as property configuration "Properties". Use this
     * annotation to propose properties and compile them into "Properties" one by
     * one, which can then be synchronized to ZK and saved.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ExtractionFieldMark {
        /**
         * Chinese des
         * 
         * @return
         */
        String descriptionCn() default "";

        /**
         * Englis des
         * 
         * @return
         */
        String descriptionEn() default "";
    }

    /**
     * Only support java type
     */
    public static enum FieldType {
        INT("INT") {
            @Override
            public Object getValueFromString(String value) {
                return Integer.parseInt(value);  // 转换为 Integer
            }
        },
        LONG("LONG") {
            @Override
            public Object getValueFromString(String value) {
                return Long.parseLong(value);  // 转换为 Long
            }
        },
        STRING("STRING") {
            @Override
            public Object getValueFromString(String value) {
                return value;  // 直接返回字符串
            }
        },
        BOOLEAN("BOOLEAN") {
            @Override
            public Object getValueFromString(String value) {
                return Boolean.parseBoolean(value);  // 转换为 Boolean
            }
        };

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
        
        public abstract Object getValueFromString(String value);
    }

    @Data
    public static class TemplateField {
        private String desCn;
        private String desEn;
        private FieldType type;
    }

    private static class Holder {
        private static final ExtractionField INSTANCE = new ExtractionField();
    }

    private ExtractionField() {
    }

    public static ExtractionField getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Get the fields to a Properties type from obj that used the ZookeeperField
     * marked
     * 
     * @param obj
     * @return
     */
    public Map<String, TemplateField> extractionTemplateMapFromObject(Object obj) {
        Map<String, TemplateField> templateMap = new HashMap<>(10);
        if (null == obj) {
            return templateMap;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            ExtractionFieldMark extractionFieldMark = field.getAnnotation(ExtractionFieldMark.class);
            if (null == extractionFieldMark) {
                continue;
            }
            FieldType fieldType = FieldType.getType(field.getType());
            if (null == fieldType) {
                continue;
            }
            TemplateField templateField = new TemplateField();
            templateField.setDesCn(extractionFieldMark.descriptionCn());
            templateField.setDesEn(extractionFieldMark.descriptionEn());
            templateField.setType(fieldType);
            templateMap.put(field.getName(), templateField);
        }
        return templateMap;
    }

    public Properties extractionPropertiesFromObject(Object obj) {
        Properties properties = new Properties();
        if (null == obj) {
            return properties;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            ExtractionFieldMark extractionFieldMark = field.getAnnotation(ExtractionFieldMark.class);
            if (null == extractionFieldMark) {
                continue;
            }
            FieldType fieldType = FieldType.getType(field.getType());
            if (null == fieldType) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    properties.put(field.getName(), value.toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    public void populateObjectFromProperties(Properties pro, Object obj) {
        if (null == pro) {
            return;
        }
        if (null == obj) {
            return;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            ExtractionFieldMark extractionFieldMark = field.getAnnotation(ExtractionFieldMark.class);
            if (null == extractionFieldMark) {
                continue;
            }
            FieldType fieldType = FieldType.getType(field.getType());
            if (null == fieldType) {
                continue;
            }
            Object value = pro.get(field.getName());
            if (!(value instanceof String)) {
                continue;
            }
            String strValue = (String) value;
            field.setAccessible(true);
            try {
                field.set(obj, fieldType.getValueFromString(strValue));
            } catch (Exception e) {

            }
        }
    }
}
