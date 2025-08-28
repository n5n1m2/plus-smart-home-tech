package collector.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Service
@RequiredArgsConstructor
public class HubService {

    private final Producer<String, SpecificRecordBase> producer;

    @Value("${kafka.topic.hubs}")
    private String topic;

    public void sendToKafka(HubEventAvro event) {
        producer.send(new ProducerRecord<>(topic, event.getHubId(), event));
    }
}
