package analyzer.service.handlers.snapshot;

import analyzer.model.Condition;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorExtractor implements BaseSnapshotHandler<TemperatureSensorAvro> {
    @Override
    public Class<TemperatureSensorAvro> payloadType() {
        return TemperatureSensorAvro.class;
    }

    @Override
    public Integer extractValue(Condition condition, TemperatureSensorAvro sensorAvro) {
        return sensorAvro.getTemperatureC();
    }
}
