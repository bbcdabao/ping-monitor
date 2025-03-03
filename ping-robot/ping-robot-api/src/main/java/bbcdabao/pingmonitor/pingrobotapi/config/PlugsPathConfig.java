package bbcdabao.pingmonitor.pingrobotapi.config;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.annotation.PostConstruct;
import lombok.Data;

@ConfigurationProperties(prefix = "robot")
@Data
public class PlugsPathConfig {

    private String plugsPath = "bbcdabao.pingmonitor.pingrobotman";

    @PostConstruct
    public void init() {
        INSTANCE_REF.set(this);
    }

    private static AtomicReference<PlugsPathConfig> INSTANCE_REF = new AtomicReference<>();

    public static PlugsPathConfig getInstance() {
        return INSTANCE_REF.get();
    }
}
