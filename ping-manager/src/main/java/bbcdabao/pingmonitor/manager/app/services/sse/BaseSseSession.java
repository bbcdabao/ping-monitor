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

package bbcdabao.pingmonitor.manager.app.services.sse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import bbcdabao.pingmonitor.common.infra.zkclientframe.BaseEventHandler;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public abstract class BaseSseSession extends BaseEventHandler {

    public static void startProcess(Supplier<BaseSseSession> factoryGet) throws Exception {
        factoryGet.get().doProcess();
    }

    private static final String SSE_SEP = "\r\n\r\n";
    private static final String SSE_SCP = "\r\n";

    private ServletOutputStream outputStream;

    public abstract void doProcess() throws Exception;

    public BaseSseSession(HttpServletResponse response) {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
            gameOver();
            return;
        }
    }

    public void sendMessage(String message, SSEvent eventType) {
        String msgSend = null;
        if (null == message) {
            msgSend = new StringBuilder()
                    .append("event: ")
                    .append(eventType.toString())
                    .append(SSE_SEP)
                    .toString();
        } else {
            msgSend = new StringBuilder()
                    .append("event: ")
                    .append(eventType.toString())
                    .append(SSE_SCP)
                    .append("data: ")
                    .append(message)
                    .append(SSE_SEP)
                    .toString();
        }
        try {
            outputStream.write(msgSend.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            gameOver();
        } 
    }

    public void onIdl() throws Exception {
        try {
            sendMessage(null, SSEvent.HEARTBEAT);
        } catch (Exception e) {
            gameOver();
        }
    }
}