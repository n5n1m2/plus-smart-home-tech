package aggregator.kafka;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Producer {
    private final org.apache.kafka.clients.producer.Producer<String, SpecificRecordBase> producer;

    public void sendToKafka(ProducerRecord<String, SpecificRecordBase> record) {
        producer.send(record, callback(record.key()));
    }

    private Callback callback(String key) {
        return (RecordMetadata metadata, Exception exception) -> {
            if (exception != null) {
                log.error("Error sending record. Key {}. Exception: {} ", key, exception.getMessage());
            }
        };
    }

    @PreDestroy
    void shutdown() {
        producer.flush();
        producer.close();
    }
}
