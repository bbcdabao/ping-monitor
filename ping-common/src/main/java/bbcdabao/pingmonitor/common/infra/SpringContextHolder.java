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

package bbcdabao.pingmonitor.common.infra;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }
    public static <T> T getBean(String beanName, Class<T> clazz) {
        Object bean = context.getBean(beanName);
        if (!clazz.isInstance(bean)) {
            throw new IllegalArgumentException("Bean '" + beanName + "' is not of type " + clazz.getName());
        }
        return clazz.cast(bean);
    }
}
