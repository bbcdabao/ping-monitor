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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.pingmonitor.common.domain.extraction.annotation.ExtractionFieldMark;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;

public class DnsResolveAddr implements IPingMoniterPlug {

    private final Logger logger = LoggerFactory.getLogger(DnsResolveAddr.class);

    @ExtractionFieldMark(descriptionCn = "域名", descriptionEn = "Domain Name")
    private String domainName;

    @Override
    public String doPingExecute(int timeOutMs) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("dns:");
        sb.append(domainName);
        try {
            InetAddress[] addresses = InetAddress.getAllByName(domainName);
            sb.append(":ok");
            for (InetAddress address : addresses) {
                sb.append(":").append(address.getHostAddress());
            }
            logger.info("DnsResolveAddr:{} resolved successfully", domainName);
            return sb.toString();
        } catch (UnknownHostException e) {
            logger.warn("DnsResolveAddr:{} could not resolve", domainName);
            throw new Exception("Domain name resolution failed for: " + domainName, e);
        }
    }
}
