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

package bbcdabao.pingmonitor.managerweb.security;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import bbcdabao.pingmonitor.common.app.module.TokenPayload;
import bbcdabao.pingmonitor.common.app.services.IParseTokenOpt;
import lombok.Data;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * 权限配置，为了安全
     */
    @Data
    private static class AuthInfo {
        private String[] paths;
        private String[] auths;
    }

    @Component
    @ConfigurationProperties(prefix = "authorize")
    @Data
    private static class AuthorizeConfig {
        private String[] openPaths;
        private AuthInfo[] authInfos;
    }
 
    /**
     * 对接自定义的JWT
     * 1. 未认证时使用，只包含token，权限为空，authenticated=false
     * 2. 认证成功后使用，带上principal和权限集合，authenticated=true
     */
    private class PtTokenAuthentication extends AbstractAuthenticationToken {
        private static final long serialVersionUID = 1L;
        private final String token;
        private final TokenPayload principal;

        public PtTokenAuthentication(String token) {
            super(null);
            this.token = token;
            this.principal = null;
            setAuthenticated(false);
        }

        public PtTokenAuthentication(TokenPayload principal, String token) {
            super(principal.getPermissions().stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            this.token = token;
            this.principal = principal;
            setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return token;
        }

        @Override
        public TokenPayload getPrincipal() {
            return principal;
        }
    }

    @Autowired
    private IParseTokenOpt jwtService;

    @Autowired
    private AuthorizeConfig authorizeConfig;
    
    /**
     * 关键点: 校验token
     * @return
     */
    private ReactiveAuthenticationManager jwtReactiveAuthenticationManager() {
        return authentication -> {
            String token = (String) authentication.getCredentials();
            try {
                TokenPayload payload = jwtService.parseToken(token);
                return Mono.just(new PtTokenAuthentication(payload, token));
            } catch (Exception e) {
                return Mono.error(new BadCredentialsException("Invalid ptToken", e));
            }
        };
    }

    /**
     * 关键点: 提取token
     * @return
     */
    private ServerAuthenticationConverter jwtServerAuthenticationConverter() {
        return exchange -> {
            HttpCookie httpCookie = exchange.getRequest().getCookies().getFirst("ptToken");
            return Mono
                    .justOrEmpty(httpCookie)
                    .map(cookie -> cookie.getValue())
                    .map(PtTokenAuthentication::new);
        };
    }

    private AuthenticationWebFilter createJwtSubFilter() {
        AuthenticationWebFilter jwtFilter =
                new AuthenticationWebFilter(jwtReactiveAuthenticationManager());
        jwtFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter());
        jwtFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        return jwtFilter;
    }

    @Bean
    ServerAuthenticationEntryPoint unauthorizedEntryPoint() {
        return (exchange, ex) -> {
            ServerWebExchange responseExchange = exchange.mutate().build();
            responseExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            responseExchange.getResponse().getHeaders().remove("WWW-Authenticate");
            responseExchange.getResponse().getHeaders().set("Content-Type", "application/json");
            byte[] bytes = "{\"error\":\"Unauthorized\"}".getBytes();
            return responseExchange.getResponse()
                    .writeWith(Mono.just(responseExchange.getResponse()
                            .bufferFactory().wrap(bytes)));
        };
    }
    @Bean
    ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            exchange.getResponse().getHeaders().set("Content-Type", "application/json");
            byte[] bytes = "{\"error\":\"Forbidden aaaaa\"}".getBytes();
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory().wrap(bytes)));
        };
    }
    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(exceptionHandling ->
                    exceptionHandling
                    .authenticationEntryPoint(unauthorizedEntryPoint())
                    .accessDeniedHandler(accessDeniedHandler())
                 )
                .authorizeExchange(exchanges -> {
                    Optional.ofNullable(authorizeConfig.getOpenPaths())
                    .ifPresent(openPaths -> {
                        exchanges.pathMatchers(openPaths).permitAll();
                    });
                    Optional.ofNullable(authorizeConfig.getAuthInfos())
                    .ifPresent(authInfos -> {
                        for (AuthInfo authInfo : authInfos) {
                            Optional.ofNullable(authInfo.getPaths())
                            .ifPresent(paths -> {
                                exchanges.pathMatchers(paths).hasAnyAuthority(authInfo.getAuths());
                            });
                        }
                    });
                    exchanges.anyExchange().authenticated();
                })
                .addFilterAt(createJwtSubFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}