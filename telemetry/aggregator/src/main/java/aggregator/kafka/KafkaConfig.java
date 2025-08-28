package aggregator.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

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
    public Consumer<String, SensorEventAvro> kafkaConsumer() {
        Properties props = new Properties();
        props.putAll(configurator.getConsumer());
        return new KafkaConsumer<>(props);
    }
}
