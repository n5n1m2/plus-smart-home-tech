package analyzer.service.handlers.snapshot;

import analyzer.model.Condition;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorExtractor implements BaseSnapshotHandler<MotionSensorAvro> {
    @Override
    public Class<MotionSensorAvro> payloadType() {
        return MotionSensorAvro.class;
    }

    @Override
    public Integer extractValue(Condition condition, MotionSensorAvro sensorAvro) {
        return sensorAvro.getMotion() ? 1 : 0;
    }
}
