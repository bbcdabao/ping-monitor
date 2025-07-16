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

package bbcdabao.pingmonitor.common.app.services.impl;

import java.security.Key;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import bbcdabao.pingmonitor.common.app.module.TokenPayload;
import bbcdabao.pingmonitor.common.app.services.IMakeTokenOpt;
import bbcdabao.pingmonitor.common.app.services.IParseTokenOpt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtService implements IMakeTokenOpt, IParseTokenOpt {

    private String secret;
    private long expiration;

    private Key key;

    public JwtService(String secret, long expiration) {
        this.secret = secret;
        this.expiration = expiration;
        
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String makeToken(TokenPayload tokenPayload) {
        Date nowTime = new Date();
        Date endTime = new Date(nowTime.getTime() + expiration);
        return Jwts
                .builder()
                .setSubject(tokenPayload.getId())
                .claim("permissions", tokenPayload.getPermissions())
                .setIssuedAt(nowTime)
                .setExpiration(endTime)
                .signWith(key)
                .compact();
    }
    private static final ObjectMapper objectMapper = new ObjectMapper(); 
    public TokenPayload parseToken(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setId(claims.getSubject());
        Object permissionsObj = claims.get("permissions");
        List<String> permissions = objectMapper.convertValue(
            permissionsObj,
            new TypeReference<List<String>>() {}
        );
        tokenPayload.setPermissions(permissions);
        return tokenPayload;
    }
}