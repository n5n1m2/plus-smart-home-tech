package analyzer.service.handlers.hub;

import analyzer.model.*;
import analyzer.storage.ScenarioRepository;
import analyzer.storage.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScenarioAddedEventHandler implements BaseHubEventHandler<ScenarioAddedEventAvro> {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;

    @Override
    public Class<ScenarioAddedEventAvro> payloadType() {
        return ScenarioAddedEventAvro.class;
    }

    @Override
    public void handle(String hubId, ScenarioAddedEventAvro payload) {
        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(payload.getName());

        Set<String> sensorIds = new HashSet<>();
        payload.getConditions().forEach(c -> sensorIds.add(c.getSensorId()));
        payload.getActions().forEach(a -> sensorIds.add(a.getSensorId()));

        Map<String, Sensor> existingSensors = sensorRepository.findAllById(sensorIds).stream()
                .collect(Collectors.toMap(Sensor::getId, Function.identity()));

        List<Sensor> newSensors = sensorIds.stream()
                .filter(id -> !existingSensors.containsKey(id))
                .map(id -> {
                    Sensor s = new Sensor();
                    s.setId(id);
                    s.setHubId(hubId);
                    return s;
                })
                .toList();

        if (!newSensors.isEmpty()) {
            sensorRepository.saveAll(newSensors);
            newSensors.forEach(s -> existingSensors.put(s.getId(), s));
        }

        payload.getConditions().forEach(conditionAvro -> {
            String sensorId = conditionAvro.getSensorId();

            Object rawValue = conditionAvro.getValue();

            Condition condition = new Condition();
            condition.setType(ConditionType.valueOf(conditionAvro.getType().name()));
            condition.setOperation(conditionAvro.getOperation().name());

            switch (rawValue) {
                case Integer i -> condition.setValueInt(i);
                case Boolean b -> condition.setValueBool(b);
                case null -> log.error("Unknown condition type: null");
                default -> log.error("Unknown condition type {}. Class: {}", rawValue, rawValue.getClass());
            }


            scenario.getConditions().put(sensorId, condition);
        });

        payload.getActions().forEach(actionAvro -> {
            String sensorId = actionAvro.getSensorId();

            Action action = new Action();
            action.setType(actionAvro.getType().name());

            if (actionAvro.getValue() instanceof Integer i) {
                action.setValue(i);
            }

            scenario.getActions().put(sensorId, action);
        });

        scenarioRepository.save(scenario);
    }
}
