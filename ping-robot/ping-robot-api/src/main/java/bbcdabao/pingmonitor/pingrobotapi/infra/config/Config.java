package bbcdabao.pingmonitor.pingrobotapi.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bbcdabao.pingmonitor.pingrobotapi.infra.domainconfig.configs.RobotConfig;

@Configuration
public class Config {
    @Bean
    RobotConfig getRobotConfig() {
        return new RobotConfig();
    }
}
