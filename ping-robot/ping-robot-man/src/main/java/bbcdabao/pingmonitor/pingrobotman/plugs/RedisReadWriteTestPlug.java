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

package bbcdabao.pingmonitor.pingrobotman.plugs;

import redis.clients.jedis.Jedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bbcdabao.pingmonitor.common.domain.extraction.annotation.ExtractionFieldMark;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;

public class RedisReadWriteTestPlug implements IPingMoniterPlug {

    private final Logger logger = LoggerFactory.getLogger(RedisReadWriteTestPlug.class);

    @ExtractionFieldMark(descriptionCn = "Redis 主机地址", descriptionEn = "Redis Host")
    private String redisHost = "localhost";

    @ExtractionFieldMark(descriptionCn = "Redis 端口", descriptionEn = "Redis Port")
    private int redisPort = 6379;

    @ExtractionFieldMark(descriptionCn = "连接超时时间", descriptionEn = "Timeout (ms)")
    private int redisTimeout = 5000;

    private static final String TEST_KEY = "redis_test_key";
    private static final String TEST_VALUE = "redis_test_value";

    @Override
    public String doPingExecute(int timeOutMs) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("redisReadWriteTest:");
        sb.append(redisHost);
        sb.append(":");
        sb.append(redisPort);
        sb.append(":");

        try (Jedis jedis = new Jedis(redisHost, redisPort, redisTimeout)) {
            jedis.set(TEST_KEY, TEST_VALUE);
            logger.info("RedisWriteTest: Write success to key: {}", TEST_KEY);
            String value = jedis.get(TEST_KEY);
            if (TEST_VALUE.equals(value)) {
                sb.append("ok");
                logger.info("RedisReadWriteTest: Read success, key: {}, value: {}", TEST_KEY, value);
            } else {
                logger.error("RedisReadWriteTest: Read failure, expected: {}, but got: {}", TEST_VALUE, value);
                throw new Exception("Redis read failed, data mismatch.");
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error("RedisReadWriteTest failed: {}", e.getMessage());
            throw new Exception("Redis read/write test failed: " + e.getMessage());
        }
    }
}
