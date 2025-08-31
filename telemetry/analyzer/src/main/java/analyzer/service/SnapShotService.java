package analyzer.service;

import analyzer.model.Condition;
import analyzer.model.Scenario;
import analyzer.model.mappers.DeviceActionRequestMapper;
import analyzer.service.handlers.snapshot.BaseSnapshotHandler;
import analyzer.storage.ScenarioRepository;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SnapShotService {

    private final Map<Class<?>, BaseSnapshotHandler<?>> extractors;
    private final ScenarioRepository scenarioRepository;
    private final HubRouter hubRouter;

    public SnapShotService(
                             Set<BaseSnapshotHandler<?>> extractors,
                             ScenarioRepository scenarioRepository,
                             HubRouter hubRouter) {
        this.extractors = extractors.stream()
                .collect(Collectors.toMap(BaseSnapshotHandler::payloadType, Function.identity()));
        this.scenarioRepository = scenarioRepository;
        this.hubRouter = hubRouter;
    }

    @Transactional
    public void process(SensorsSnapshotAvro snapshotAvro) {
        Map<String, SensorStateAvro> states = snapshotAvro.getSensorsState();
        List<Scenario> scenarios = scenarioRepository.findByHubIdWithConditions(snapshotAvro.getHubId());
        if (scenarios.isEmpty()) {
            return;
        }


        for (Scenario scenario : scenarios) {
            boolean match = scenario.getConditions().entrySet().stream()
                    .allMatch(entry -> {
                        String sensorId = entry.getKey();
                        Condition condition = entry.getValue();
                        SensorStateAvro state = states.get(sensorId);
                        return state != null && getCondition(condition, state);
                    });
            if (match) {
                scenario.getActions().forEach((sensorId, action) -> {
                    DeviceActionRequest request = DeviceActionRequestMapper.map(
                            scenario, snapshotAvro.getHubId(), sensorId, action
                    );
                    hubRouter.sendDeviceActionRequest(request);
                });
            }
        }
    }

    private boolean getCondition(Condition condition, SensorStateAvro state) {
        Integer actual = getValue(condition, state.getData());
        Integer expected = condition.getValueInt();
        Boolean expectedBool = condition.getValueBool();


        if (expected == null && expectedBool != null) {
            expected = expectedBool ? 1 : 0;
        }

        if (actual == null || expected == null) {
            return false;
        }

        return switch (condition.getOperation()) {
            case "EQUALS" -> actual.equals(expected);
            case "GREATER_THAN" -> actual > expected;
            case "LOWER_THAN" -> actual < expected;
            default -> false;
        };
    }

    private Integer getValue(Condition condition, Object payload) {
        BaseSnapshotHandler<Object> extractor = (BaseSnapshotHandler<Object>) extractors.get(payload.getClass());
        if (extractor == null) {
            throw new RuntimeException(payload.getClass() + " is not supported");
        } else {
            return extractor.extractValue(condition, payload);
        }
    }
}
