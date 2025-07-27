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

package bbcdabao.pingmonitor.common.infra.coordination;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

/**
 * RsType is define the ping resoult to save where.
 * 1 INNER type is in the zookeeper inner for path /result/...
 * 2 REDIS type is in the redis for save the ping result
 */
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

    /**
     * Currently only the results are considered to be saved in Zookeeper
     * 目前只考虑结果保存在zookeeper里面
     */
    private RsType rsType = RsType.INNER;
    private RedisConfig redisConfig = null;

    /**
     * Ping test cycle
     * 拨测周期
     */
    private String cronTask;

    /**
     * Master assign task detection cycle
     * 选主周期
     */
    private String cronMain;

    /**
     * Ping time out
     * 拨测超时
     */
    private int timeOutMs;

    /**
     * Whether to overwrite the configuration in zookeeper
     * 是否覆盖zookeeper里面的配置
     */
    private boolean isOverwrite = false;
}