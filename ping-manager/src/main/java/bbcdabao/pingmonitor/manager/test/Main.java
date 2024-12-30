package bbcdabao.pingmonitor.manager.test;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class Main extends Thread implements ApplicationRunner {
    //@Autowired
    //private CuratorFramework curatorFramework;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        start();
    }
}
