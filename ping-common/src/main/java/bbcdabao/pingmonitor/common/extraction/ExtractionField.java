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

package bbcdabao.pingmonitor.common.extraction;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import bbcdabao.pingmonitor.common.extraction.annotation.ExtractionFieldMark;
import jakarta.validation.constraints.NotNull;

/**
 * Extraction field that used ZookeeperField labeled.
 */
public class ExtractionField {

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
    public Map<String, TemplateField> extractionTemplateMapFromObject(@NotNull Object obj) {
        Map<String, TemplateField> templateMap = new HashMap<>(10);
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

    public Properties extractionPropertiesFromObject(@NotNull Object obj) throws IllegalAccessException {
        Properties properties = new Properties();
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
            Object value = field.get(obj);
            if (value != null) {
                properties.put(field.getName(), value.toString());
            }
        }
        return properties;
    }

    public void populateObjectFromProperties(@NotNull Properties pro, @NotNull Object obj) throws IllegalAccessException {
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
            field.set(obj, fieldType.getValueFromString(strValue));
        }
    }
}
