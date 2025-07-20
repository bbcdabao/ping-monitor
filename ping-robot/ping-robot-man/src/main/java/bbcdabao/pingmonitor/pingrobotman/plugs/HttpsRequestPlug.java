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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.pingmonitor.common.domain.extraction.annotation.ExtractionFieldMark;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;

public class HttpsRequestPlug implements IPingMoniterPlug {

    private final Logger logger = LoggerFactory.getLogger(HttpsRequestPlug.class);

    @ExtractionFieldMark(descriptionCn = "请求地址", descriptionEn = "Request URL")
    private String requestUrl;

    @ExtractionFieldMark(descriptionCn = "请求方式", descriptionEn = "Method")
    private String method = "GET";

    @ExtractionFieldMark(descriptionCn = "请求体", descriptionEn = "Request Body")
    private String requestBody;

    private static final Map<String, Boolean> METHOD_OUTPUT_MAP = Map.of(
            "GET", false,
            "POST", true,
            "PUT", true,
            "DELETE", false);

    @Override
    public String doPingExecute(int timeOutMs) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("https:");
        sb.append(requestUrl);
        sb.append(":");
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String methodUpperCase = method.toUpperCase();
        Boolean doOutput = METHOD_OUTPUT_MAP.get(methodUpperCase);
        if (doOutput == null) {
            throw new Exception("method param error:" + method);
        }
        sb.append(methodUpperCase);
        conn.setConnectTimeout(timeOutMs);
        conn.setReadTimeout(timeOutMs);
        conn.setRequestMethod(methodUpperCase);
        conn.setDoOutput(doOutput);
        if (doOutput.booleanValue()) {
            conn.setRequestProperty("Content-Type", "application/json");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("UTF-8"));
            }    
        }
        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            sb.append("ok");
            String retInfo = sb.toString();
            logger.info("HttpsRequestPlug ok:{}", retInfo);
            return retInfo;
        }
        logger.warn("HttpsRequestPlug:{}:{}:bad response code:{}", method, requestUrl, responseCode);
        throw new Exception("HTTPS request failed with code: " + responseCode);
    }
}
