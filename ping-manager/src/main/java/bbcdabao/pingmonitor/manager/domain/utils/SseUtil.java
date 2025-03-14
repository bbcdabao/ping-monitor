package bbcdabao.pingmonitor.manager.domain.utils;

import jakarta.servlet.http.HttpServletResponse;

public class SseUtil {
    public static final String SSE_SEP = "\n\n";
    public static void initSseHttpServletResponse(HttpServletResponse response) {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
    }
}
