package aggregator.service;

import aggregator.kafka.Producer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AggregatorService {
    private final Producer producer;
    private final Map<String, SensorsSnapshotAvro> sensorsSnapshotAvroMap = new HashMap<>();
    private final Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    @Value("${kafka.topic.snapshot}")
    private String topic;
    private final Consumer<String, SensorEventAvro> consumer;

    public void handle(ConsumerRecords<String, SensorEventAvro> records) {
        for (ConsumerRecord<String, SensorEventAvro> record : records) {
            aggregateSensorEvent(record.value()).ifPresent(value -> {
                producer.sendToKafka(new ProducerRecord<>(topic, value.getHubId(), value));
            });

            offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
        }
        consumer.commitAsync(offsets, null);
    }

    private Optional<SensorsSnapshotAvro> aggregateSensorEvent(SensorEventAvro sensorEventAvro) {


        SensorsSnapshotAvro sensorsSnapshotAvro = sensorsSnapshotAvroMap.computeIfAbsent(sensorEventAvro.getHubId(), key -> {
            return SensorsSnapshotAvro.newBuilder()
                    .setHubId(key)
                    .setTimestamp(Instant.ofEpochMilli(sensorEventAvro.getTimestamp()))
                    .setSensorsState(new HashMap<>())
                    .build();
        });

        Map<String, SensorStateAvro> sensorStateAvroMap = sensorsSnapshotAvro.getSensorsState();
        SensorStateAvro old = sensorStateAvroMap.get(sensorEventAvro.getId());

        if (old != null) {
            if (Instant.ofEpochMilli(sensorEventAvro.getTimestamp()).isBefore(old.getTimestamp()) ||
                    sensorEventAvro.getPayload().equals(old.getData())) {
                return Optional.empty();
            }
        }
        SensorStateAvro updState = new SensorStateAvro();
        updState.setTimestamp(Instant.ofEpochMilli(sensorEventAvro.getTimestamp()));
        updState.setData(sensorEventAvro.getPayload());

        sensorStateAvroMap.put(sensorEventAvro.getId(), updState);

        sensorsSnapshotAvro.setTimestamp(Instant.ofEpochMilli(sensorEventAvro.getTimestamp()));
        return Optional.of(sensorsSnapshotAvro);
    }

    public void shutdown() {
        consumer.commitSync(offsets);
        consumer.close();
    }
}
