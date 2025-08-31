package analyzer.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaPropertiesConfigurator configurator;

    @Bean
    public Producer<String, SpecificRecordBase> kafkaProducer() {
        Properties props = new Properties();
        props.putAll(configurator.getProducer());
        return new KafkaProducer<>(props);
    }

    @Bean
    public Consumer<String, SensorsSnapshotAvro> kafkaSnapshotConsumer() {
        Properties props = new Properties();
        props.putAll(configurator.getConsumer("snapshot"));
        return new KafkaConsumer<>(props);
    }

    @Bean
    public Consumer<String, HubEventAvro> kafkaHubEventConsumer() {
        Properties props = new Properties();
        props.putAll(configurator.getConsumer("hub"));
        return new KafkaConsumer<>(props);
    }
}
