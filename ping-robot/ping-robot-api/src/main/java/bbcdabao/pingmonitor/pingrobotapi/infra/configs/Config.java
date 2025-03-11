package bbcdabao.pingmonitor.pingrobotapi.infra.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    RobotConfig getRobotConfig() {
        return new RobotConfig();
    }
}
