package analyzer.service.handlers.hub;

import analyzer.storage.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
@RequiredArgsConstructor
public class ScenarioRemovedHandler implements BaseHubEventHandler<ScenarioRemovedEventAvro> {
    private final ScenarioRepository scenarioRepository;

    @Override
    public Class<ScenarioRemovedEventAvro> payloadType() {
        return ScenarioRemovedEventAvro.class;
    }

    @Override
    public void handle(String hubId, ScenarioRemovedEventAvro payload) {
        scenarioRepository.deleteByHubIdAndName(hubId, payload.getName());
    }
}
