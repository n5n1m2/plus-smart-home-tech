package collector.model.sensors.handlers;

import collector.service.SensorEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final SensorEventService service;

    @Override
    public SensorEventProto.PayloadCase getPayloadCase() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        LightSensorProto lightSensorProto = event.getLightSensorEvent();

        LightSensorAvro avro = LightSensorAvro.newBuilder()
                .setLuminosity(lightSensorProto.getLuminosity())
                .setLinkQuality(lightSensorProto.getLinkQuality())
                .build();

        service.sendToKafka(SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp().getNanos())
                .setPayload(avro)
                .build()
        );
    }
}
