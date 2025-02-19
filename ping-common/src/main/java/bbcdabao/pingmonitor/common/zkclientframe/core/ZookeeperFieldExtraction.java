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

package bbcdabao.pingmonitor.common.zkclientframe.core;

import java.lang.reflect.Field;
import java.util.Properties;

import bbcdabao.pingmonitor.common.zkclientframe.annotation.ZookeeperField;
import bbcdabao.pingmonitor.common.zkclientframe.zkdataobj.FieldType;
import bbcdabao.pingmonitor.common.zkclientframe.zkdataobj.TemplateField;

/**
 * Extraction field that used ZookeeperField labeled.
 */
public class ZookeeperFieldExtraction {

    private static class Holder {
        private static final ZookeeperFieldExtraction INSTANCE = new ZookeeperFieldExtraction();
    }

    private ZookeeperFieldExtraction() {
    }

    public static ZookeeperFieldExtraction getInstance() {
        return Holder.INSTANCE;
    }

    public Properties extractionObjectTemplate(Object obj) {
        Properties properties = new Properties();
        if (null == obj) {
            return properties;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            ZookeeperField zookeeperField = field.getAnnotation(ZookeeperField.class);
            if (null == zookeeperField) {
                continue;
            }
            FieldType fieldType = FieldType.getType(field.getType());
            if (null == fieldType) {
                continue;
            }
            TemplateField templateField = new TemplateField();
            templateField.setDesCn(zookeeperField.descriptionCn());
            templateField.setDesEn(zookeeperField.descriptionEn());
            templateField.setType(fieldType);
            properties.put(field.getName(), templateField);
        }
        return properties;
    }

    public void initObject(Properties pro, Object obj) {
    }
}
