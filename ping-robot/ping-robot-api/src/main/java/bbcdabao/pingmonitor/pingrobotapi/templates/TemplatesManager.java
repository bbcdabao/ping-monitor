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

package bbcdabao.pingmonitor.pingrobotapi.templates;

public class TemplatesManager {

    private static class Holder {
        private static final TemplatesManager INSTANCE = getTemplatesManagerInstance();
    }

    private static TemplatesManager getTemplatesManagerInstance() {
        ClassLoader classLoader = MetaInfPropertiesReader.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream("META-INF/config.properties")) {
            if (inputStream == null) {
                System.out.println("配置文件未找到");
                return;
            }

            // 创建 Properties 对象
            Properties properties = new Properties();
            
            // 加载配置文件
            properties.load(inputStream);
            
            // 打印配置文件中的内容
            properties.forEach((key, value) -> System.out.println(key + "=" + value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TemplatesManager getInstance() {
        return Holder.INSTANCE;
    }

    private TemplatesManager(String scanPatch) {
    }
}
