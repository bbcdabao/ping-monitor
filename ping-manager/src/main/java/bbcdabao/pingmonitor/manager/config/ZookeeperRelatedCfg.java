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

package bbcdabao.pingmonitor.manager.config;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Zookeeper related configuration
 */
@Configuration
public class ZookeeperRelatedCfg {

	@Value("${zk-server.mode:NONE}")
	private String zkServerMode;

	@Value("${zk-config.path:}")
	private String zkConfigPath;

    @Autowired
    private Environment environment;

	private Properties getSingleZookeeperServerCfg() throws Exception {
		Properties props = new Properties();
		if (!StringUtils.isEmpty(zkConfigPath)) {
			InputStream is = ZookeeperRelatedCfg.class.getResourceAsStream(zkConfigPath);
			props.load(is);
			return props;
		}
		props.setProperty("tickTime", environment.getProperty("tickTime", "2000"));
		props.setProperty("dataDir", environment.getProperty("dataDir", 
				new File(System.getProperty("java.io.tmpdir"), "zookeeper").getAbsolutePath()));
		props.setProperty("clientPort", environment.getProperty("clientPort", "2181"));
		props.setProperty("initLimit", environment.getProperty("initLimit", "10"));
		props.setProperty("syncLimit", environment.getProperty("syncLimit", "5"));
		props.setProperty("admin.enableServer", "true");
		props.setProperty("admin.serverPort", "8181");
		props.setProperty("admin.commandURL", "/commands");
		
		
		
		return props;
	}

    @Bean
    @ConditionalOnProperty(prefix = "zk-server", name= "mode", havingValue = "SINGLE", matchIfMissing = false)
    ZooKeeperServerMain getSingleZookeeperServer() throws Exception {
        QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
        quorumConfig.parseProperties(getSingleZookeeperServerCfg());
 
        ZooKeeperServerMain zkServer = new ZooKeeperServerMain();
        ServerConfig config = new ServerConfig();
        config.readFrom(quorumConfig);
        zkServer.runFromConfig(config);
    	return zkServer;
    }

    @Bean
    @ConditionalOnProperty(prefix = "zk-server", name= "mode", havingValue = "CLUSTER", matchIfMissing = false)
    ZooKeeperServerMain getClusterZookeeperServer() throws Exception {
    	throw new Exception("not support zk-server.mode=CLUSTER");
    }
}
