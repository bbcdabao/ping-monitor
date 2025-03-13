/*
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

package bbcdabao.pingmonitor.manager.infra.configs;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.Data;

/**
 * Zookeeper related configuration
 */
@Configuration
public class ZookeeperRelatedCfg {

    /**
     * Zookeeper inner config define
     */
    @Component
    @ConfigurationProperties(prefix = "zk-server")
    @Data
    private static class ZkServersCfg {
        private String zkConfigPath = "";
        private String clientPort = "2181";
        private String tickTime = "1000";
        private String dataDir = "";
        private String dataLogDir = "";
        private String maxClientCnxns = "60";
        private String initLimit = "10";
        private String syncLimit = "5";
        private String globalOutstandingLimit = "1000";
        private String preAllocSize = "1048576";
        private String snapCount = "100000";
        private String syncEnabled = "true";
        private Autopurge autopurge = new Autopurge();
        private List<String> servers;
        @Data
        private static class Autopurge {
            private String snapRetainCount = "3";
            private String purgeInterval = "24";
        }
    }

    @Autowired
    private ZkServersCfg zkServersCfg;

    private Properties getInnerZookeeperConfig() throws Exception {
        String dataDir = zkServersCfg.getDataDir();
        String dataLogDir = zkServersCfg.getDataLogDir();
        if (ObjectUtils.isEmpty(dataDir)) {
            dataDir = new File(System.getProperty("java.io.tmpdir"), "zookeeper").getAbsolutePath();
        }
        if (ObjectUtils.isEmpty(dataLogDir)) {
            dataLogDir = dataDir;
        }
        Properties props = new Properties();
        props.setProperty("clientPort", zkServersCfg.getClientPort());
        props.setProperty("tickTime", zkServersCfg.getTickTime());
        props.setProperty("dataDir", dataDir);
        props.setProperty("dataLogDir", dataLogDir);
        props.setProperty("maxClientCnxns", zkServersCfg.getMaxClientCnxns());
        props.setProperty("initLimit", zkServersCfg.getInitLimit());
        props.setProperty("syncLimit", zkServersCfg.getSyncLimit());
        props.setProperty("globalOutstandingLimit", zkServersCfg.getGlobalOutstandingLimit());
        props.setProperty("preAllocSize", zkServersCfg.getPreAllocSize());
        props.setProperty("snapCount", zkServersCfg.getSnapCount());
        props.setProperty("syncEnabled", zkServersCfg.getSyncEnabled());
        props.setProperty("autopurge.snapRetainCount", zkServersCfg.getAutopurge().getSnapRetainCount());
        props.setProperty("autopurge.purgeInterval", zkServersCfg.getAutopurge().getPurgeInterval());
        props.setProperty("snapShotCompressionEnabled", "false");
        props.setProperty("admin.enableServer", "false");
        List<String> servers = zkServersCfg.getServers();
        if (CollectionUtils.isEmpty(servers)) {
            return props;
        }
        int index = 0;
        for (String server : servers) {
            index++;
            StringBuilder sb = new StringBuilder();
            sb.append("server.");
            sb.append(index);
            props.setProperty(sb.toString(), server);
        }
        return props;
    }

	private Properties getZookeeperConfig() throws Exception {
	    String zkConfigPath = zkServersCfg.getZkConfigPath();
	    if (ObjectUtils.isEmpty(zkConfigPath)) {
	        return getInnerZookeeperConfig();
	    }
		Properties props = new Properties();

		try (InputStream is = ZookeeperRelatedCfg.class.getResourceAsStream(zkConfigPath);) {
		    props.load(is);
		}

		return props;
	}

    @Bean
    @ConditionalOnProperty(prefix = "zk-server", name= "mode", havingValue = "single", matchIfMissing = false)
    QuorumPeerConfig getSingleQuorumPeerConfig() throws Exception {
        QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
        quorumConfig.parseProperties(getZookeeperConfig());
 
        Thread zookeeperThread = new Thread(() -> {
            try {
                ZooKeeperServerMain zkServer = new ZooKeeperServerMain();
                ServerConfig config = new ServerConfig();
                config.readFrom(quorumConfig);
                zkServer.runFromConfig(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        zookeeperThread.setName("zookeeper-single-server-thread");
        zookeeperThread.start();

    	return quorumConfig;
    }

    @Bean
    @ConditionalOnProperty(prefix = "zk-server", name= "mode", havingValue = "cluster", matchIfMissing = false)
    QuorumPeerConfig getClusterleQuorumPeerConfig() throws Exception {
        QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
        quorumConfig.parseProperties(getZookeeperConfig());
        Thread zookeeperThread = new Thread(() -> {
            try {
                QuorumPeerMain zkServer = new QuorumPeerMain();
                zkServer.runFromConfig(quorumConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        zookeeperThread.setName("zookeeper-single-server-thread");
        zookeeperThread.start();
        return quorumConfig;
    }
}
