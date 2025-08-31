package analyzer.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "analyzer.kafka")
public class KafkaPropertiesConfigurator {
    private Map<String, Map<String, String>> consumers;
    private Map<String, String> producer;


    public Map<String, String> getConsumer(String name) {
        return consumers.get(name);
    }
}
