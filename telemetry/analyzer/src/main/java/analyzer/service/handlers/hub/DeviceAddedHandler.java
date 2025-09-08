package analyzer.service.handlers.hub;

import analyzer.model.Sensor;
import analyzer.storage.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;

@Component
@RequiredArgsConstructor
public class DeviceAddedHandler implements BaseHubEventHandler<DeviceAddedEventAvro> {
    private final SensorRepository sensorRepository;

    @Override
    public Class<DeviceAddedEventAvro> payloadType() {
        return DeviceAddedEventAvro.class;
    }

    @Override
    public void handle(String hubId, DeviceAddedEventAvro payload) {
        Sensor sensor = new Sensor();
        sensor.setId(payload.getId());
        sensor.setHubId(hubId);
        sensorRepository.save(sensor);
    }
}
