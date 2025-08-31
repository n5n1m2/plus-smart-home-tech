package analyzer.service.handlers.snapshot;

import analyzer.model.Condition;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorExtractor implements BaseSnapshotHandler<SwitchSensorAvro>{
    @Override
    public Class<SwitchSensorAvro> payloadType() {
        return SwitchSensorAvro.class;
    }

    @Override
    public Integer extractValue(Condition condition, SwitchSensorAvro sensorAvro) {
        return sensorAvro.getState() ? 1 : 0;
    }
}
