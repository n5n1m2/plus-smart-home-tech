package analyzer.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    private final SnapShotService service;

    @Value("${analyzer.kafka.topic.snapshots}")
    private String topic;


    @Override
    public void run() {
        consumer.subscribe(Collections.singletonList(topic));
        try {
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshotAvro = record.value();
                    try {
                        service.process(snapshotAvro);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    TopicPartition topicPartition = new TopicPartition(topic, record.partition());
                    offsets.put(topicPartition, new OffsetAndMetadata(record.offset() + 1));

                    consumer.commitSync(offsets);
                }
            }
        } catch (WakeupException e) {
            log.error("Wakeup exception", e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            consumer.commitSync(offsets);
            consumer.close();
        }
    }

    @PreDestroy
    public void destroy() {
        consumer.wakeup();
    }
}
