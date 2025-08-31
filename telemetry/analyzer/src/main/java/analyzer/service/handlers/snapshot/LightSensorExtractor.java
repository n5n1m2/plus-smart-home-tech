package analyzer.service.handlers.snapshot;

import analyzer.model.Condition;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorExtractor implements BaseSnapshotHandler<LightSensorAvro> {
    @Override
    public Class<LightSensorAvro> payloadType() {
        return LightSensorAvro.class;
    }

    @Override
    public Integer extractValue(Condition condition, LightSensorAvro sensorAvro) {
        return sensorAvro.getLuminosity();
    }
}
