/**
 * Copyright 2025 bbcdabao Team
 */

package bbcdabao.pingmonitor.manager.infra.zookeeperclient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ZookeeperConnectionStateListener implements ConnectionStateListener {
    private final Logger logger = LoggerFactory.getLogger(ZookeeperConnectionStateListener.class);
    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        logger.info("ZookeeperConnectionStateListener status:" + newState);

        if (newState == ConnectionState.LOST) {
            System.err.println("⚠️ Zookeeper 连接丢失，进行降级处理...");
            logger.info("⚠️ Zookeeper connect lose! down process");
        }

        if (newState == ConnectionState.RECONNECTED) {
            logger.info("✅ Zookeeper reconnect!");
        }
    }
}
