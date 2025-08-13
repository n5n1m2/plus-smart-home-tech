package collector.service;

import collector.model.sensors.*;
import collector.model.sensors.base.SensorEvent;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Service
@RequiredArgsConstructor
public class SensorEventService {
    private final Producer<String, SpecificRecordBase> producer;
    @Value("${topic.sensor}")
    private String topic;

    public void sendToKafka(SensorEvent sensorEvent) {
        SensorEventAvro avro = getRecord(sensorEvent);
        producer.send(new ProducerRecord<>(topic, avro.getId(), avro));
    }


    private SensorEventAvro getRecord(SensorEvent event) {
        SpecificRecord record = switch (event) {
            case ClimateSensorEvent e -> ClimateSensorAvro.newBuilder()
                    .setCo2Level(e.getCo2Level())
                    .setHumidity(e.getHumidity())
                    .setTemperatureC(e.getTemperatureC())
                    .build();
            case LightSensorEvent e -> LightSensorAvro.newBuilder()
                    .setLuminosity(e.getLuminosity())
                    .setLinkQuality(e.getLinkQuality())
                    .build();
            case MotionSensorEvent e -> MotionSensorAvro.newBuilder()
                    .setMotion(e.getMotion())
                    .setLinkQuality(e.getLinkQuality())
                    .setMotion(e.getMotion())
                    .build();
            case SmartSwitchSensorEvent e -> SwitchSensorAvro.newBuilder()
                    .setState(e.getState())
                    .build();
            case TemperatureSensorEvent e -> TemperatureSensorAvro.newBuilder()
                    .setTemperatureC(e.getTemperatureC())
                    .setTemperatureF(e.getTemperatureF())
                    .build();
            default -> throw new IllegalArgumentException("Unknown SensorType " + event);
        };

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setPayload(record)
                .build();
    }
}
