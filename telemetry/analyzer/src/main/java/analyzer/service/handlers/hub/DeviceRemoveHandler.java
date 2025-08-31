package analyzer.service.handlers.hub;

import analyzer.storage.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
@RequiredArgsConstructor
public class DeviceRemoveHandler implements BaseHubEventHandler<DeviceRemovedEventAvro> {
    private final SensorRepository sensorRepository;

    @Override
    public Class<DeviceRemovedEventAvro> payloadType() {
        return DeviceRemovedEventAvro.class;
    }

    @Override
    public void handle(String hubId, DeviceRemovedEventAvro payload) {
        sensorRepository.deleteById(payload.getId());
    }
}
