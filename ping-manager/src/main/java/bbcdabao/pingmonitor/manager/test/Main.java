package bbcdabao.pingmonitor.manager.test;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class Main extends Thread implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        start();
    }

    @Override
    public void run() {
    }
}
