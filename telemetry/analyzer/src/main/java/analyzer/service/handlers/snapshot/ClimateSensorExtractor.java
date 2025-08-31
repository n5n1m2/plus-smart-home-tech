package analyzer.service.handlers.snapshot;

import analyzer.model.Condition;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Component
public class ClimateSensorExtractor implements BaseSnapshotHandler<ClimateSensorAvro> {
    @Override
    public Class<ClimateSensorAvro> payloadType() {
        return ClimateSensorAvro.class;
    }

    @Override
    public Integer extractValue(Condition condition, ClimateSensorAvro sensorAvro) {
        return switch (condition.getType()) {
            case TEMPERATURE -> sensorAvro.getTemperatureC();
            case HUMIDITY -> sensorAvro.getHumidity();
            case CO2LEVEL -> sensorAvro.getCo2Level();
            default -> null;
        };
    }
}
