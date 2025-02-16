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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import bbcdabao.pingmonitor.common.zkclientframe.annotation.ZookeeperField;
import lombok.Data;

@Data
public class Template {
    private Collection<TemplateField> fields = new ArrayList<>();

    private static Map<String, FieldType> SUPPORT_FIELD = new HashMap<>(3);
    static {
        SUPPORT_FIELD.put("int", FieldType.INT);
        SUPPORT_FIELD.put("long", FieldType.LONG);
        SUPPORT_FIELD.put("String", FieldType.STRING);
    }

    public static Template getTemplateFromObj(Object obj) {
        Template template = new Template();
        if (obj == null) {
            return template;
        }
        Class<?> clazz = obj.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            ZookeeperField annotation = field.getAnnotation(ZookeeperField.class);
            if (null == annotation) {
                continue;
            }
            String typeName = field.getType().getSimpleName();
            FieldType fieldTypeGet = SUPPORT_FIELD.get(typeName);
            if (null == fieldTypeGet) {
                continue;
            }
            TemplateField templateField = new TemplateField();
            templateField.setType(fieldTypeGet);
            templateField.setKey(field.getName());
            templateField.setDesCn(annotation.descriptionCn());
            templateField.setDesEn(annotation.descriptionEn());
            template.fields.add(templateField);
        }
        return template;
    }
}
