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

package bbcdabao.pingmonitor.common.app.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bbcdabao.pingmonitor.common.app.services.impl.JwtService;
import bbcdabao.pingmonitor.common.app.services.impl.SecurityService;
import jakarta.annotation.PostConstruct;

@Configuration
@ConditionalOnProperty(prefix = "token", name = "enable", havingValue = "true", matchIfMissing = false)
public class Config {

    @Value("${token.jwt.secret}")
    private String secret;

    @Value("${token.jwt.expiration:0}")
    private long expiration = 0;
    
    @PostConstruct
    public void init() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("jwt.secret lenth more than 32");
        }
        expiration = (expiration * 1000) * 60;
    }
    
    @Bean("jwtService")
    JwtService getJwtService() {
        return new JwtService(secret, expiration);
    }

    @Bean("securityService")
    SecurityService getSecurityService() {
        return new SecurityService();
    }
}