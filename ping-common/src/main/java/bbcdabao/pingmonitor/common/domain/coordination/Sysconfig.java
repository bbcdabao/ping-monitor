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

package bbcdabao.pingmonitor.common.domain.coordination;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sysconfig {

    public static enum RsType {
        INNER("inner"),
        REDIS("redis");

        private final String value;
        RsType(String value) {
            this.value = value;
        }

        private static final Map<String, RsType> RSTYPE_MAP = new HashMap<>(5);
        static {
            for (RsType type : RsType.values()) {
                RSTYPE_MAP.put(type.getValue(), type);
            }
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static RsType fromValue(String value) {
            RsType type = RSTYPE_MAP.get(value);
            if (null == type) {
                throw new IllegalArgumentException("Unexpected value '" + value + "'");
            }
            return type;
        }
    }

    @Data
    public static class RedisConfig {
        private String host;
        private int port;
        private String password;
        private int database;
        private int timeout;
    }

    private RsType rsType = RsType.INNER;
    private RedisConfig redisConfig = null;
    /**
     * ping test cycle
     */
    private String cronTask;
    /**
     * master assign task detection cycle
     */
    private String cronMain;
    /**
     * ping time out
     */
    private int timeOutMs;
    
    private boolean isOverwrite = false;
}
