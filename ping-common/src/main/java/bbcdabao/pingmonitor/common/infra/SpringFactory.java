/**
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

import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class SpringFactory implements ApplicationContextAware, IBeanFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringFactory.class);
    
    private static AtomicReference<SpringFactory> INSTANCE = new AtomicReference<>(null);

    public static IBeanFactory getInstance() {
        IBeanFactory beanFactory = INSTANCE.get();
        if (beanFactory != null) {
            return beanFactory;
        }
        throw new IllegalStateException("SpringFactory has not been initialized.");
    }

    @Autowired
    private ApplicationContext context;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
        INSTANCE.set(this);
    }

    public <T> T getBean(Class<T> clazz) {
        T bean = null;
        try {
            bean = context.getBean(clazz);
        } catch (Exception e) {
            LOGGER.error("SpringFactoryImpl.getBean Exception:", e);
        }
        return bean;
    }
}