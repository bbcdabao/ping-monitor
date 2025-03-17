package bbcdabao.pingmonitor.manager.app.services.sse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public abstract class BaseSseSession extends BaseEventHandler {

    public static void startProcess(Supplier<BaseSseSession> factoryGet) throws Exception {
        factoryGet.get().doProcess();
    }

    private static final String SSE_SEP = "\n\n";

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

    public void sendMessage(String message) {
        String msgSend = null;
        if (null == message) {
            msgSend = SSE_SEP;
        } else {
            msgSend = new StringBuilder()
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
            sendMessage(null);
        } catch (Exception e) {
            gameOver();
        }
    }
}
