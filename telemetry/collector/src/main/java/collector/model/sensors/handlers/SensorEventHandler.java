package collector.model.sensors.handlers;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getPayloadCase();

    void handle(SensorEventProto event);
}
