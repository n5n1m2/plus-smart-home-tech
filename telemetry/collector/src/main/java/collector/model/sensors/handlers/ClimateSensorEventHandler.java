package collector.model.sensors.handlers;

import collector.service.SensorEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final SensorEventService service;

    @Override
    public SensorEventProto.PayloadCase getPayloadCase() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        ClimateSensorProto proto = event.getClimateSensorEvent();

        ClimateSensorAvro avro = ClimateSensorAvro.newBuilder()
                .setCo2Level(proto.getCo2Level())
                .setHumidity(proto.getHumidity())
                .setTemperatureC(proto.getTemperatureC())
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
