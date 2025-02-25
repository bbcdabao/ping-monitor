package bbcdabao.pingmonitor.manager.test;

import java.util.Properties;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import bbcdabao.pingmonitor.common.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.extraction.ExtractionField;
import bbcdabao.pingmonitor.common.zkclientframe.core.CuratorFrameworkInstance;

@Service
public class Main extends Thread implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
        MyPlug myPlug = new MyPlug();
        myPlug.setMyid(3000);
        myPlug.setInfo("fuck!");
        Properties properties = ExtractionField.getInstance().extractionPropertiesFromObject(myPlug);

        byte[] propertb = ByteDataConver.getInstance().getConvertToByteForProperties().getData(properties);

        try {
            CuratorFrameworkInstance.getInstance().create().creatingParentsIfNeeded().forPath("/nmb", propertb);
            byte[] propertread = CuratorFrameworkInstance.getInstance().getData().forPath("/nmb");
            Properties aaa = ByteDataConver.getInstance().getConvertFromByteForProperties().getValue(propertread);
            int a;
            a = 9;
        } catch (Exception e) {
            int a;
            a = 9;
        }

        MyPlug myPlugs = new MyPlug();
        ExtractionField.getInstance().populateObjectFromProperties(properties, myPlugs);
        int a;
        a = 9;
    }
}
