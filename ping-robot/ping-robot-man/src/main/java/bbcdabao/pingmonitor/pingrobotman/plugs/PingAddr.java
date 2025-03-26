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

package bbcdabao.pingmonitor.pingrobotman.plugs;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.pingmonitor.common.domain.extraction.annotation.ExtractionFieldMark;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;

public class PingAddr implements IPingMoniterPlug {

    private final Logger logger = LoggerFactory.getLogger(PingAddr.class);

    @ExtractionFieldMark(descriptionCn = "地址:", descriptionEn = "Ip addr:")
    private String ipAddr;

    @Override
    public String doPingExecute(int timeOutMs) throws Exception {
        boolean reachable = InetAddress.getByName(ipAddr).isReachable(timeOutMs);
        StringBuilder sb = new StringBuilder();
        sb.append("ping:");
        sb.append(ipAddr);
        sb.append(":");
        if (reachable) {
            logger.info("PingAddr:{}:is ok", ipAddr);
            sb.append("ok");
        } else {
            logger.info("PingAddr:{}:is not reachable", ipAddr);
            sb.append("not reachable");
        }
        return sb.toString();
    }
}
