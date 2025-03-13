package bbcdabao.pingmonitor.manager.app.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import bbcdabao.pingmonitor.manager.app.domain.handlers.RobotInfosHandler;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/robot")
public class RobotManagerController {

    private final Logger logger = LoggerFactory.getLogger(RobotManagerController.class);

    @GetMapping(value = "{robotGroupName}/infos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public void getInfos(@PathVariable("robotGroupName") String robotGroupName,
            HttpServletResponse response) throws IOException {

        RobotInfosHandler robotInfosHandler = new RobotInfosHandler(robotGroupName, response);
        
        while (true) {
            if (robotInfosHandler.isOver()) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
            logger.info("sssddd");
        }
    }
}
