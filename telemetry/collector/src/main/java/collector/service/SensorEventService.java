package collector.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Service
@RequiredArgsConstructor
public class SensorEventService {
    private final Producer<String, SpecificRecordBase> producer;
    @Value("${kafka.topic.sensor}")
    private String topic;

    public void sendToKafka(SensorEventAvro avro) {
        producer.send(new ProducerRecord<>(topic, avro.getId(), avro));
    }
}
