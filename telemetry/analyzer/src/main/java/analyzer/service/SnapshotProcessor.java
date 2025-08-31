package analyzer.service;

import analyzer.model.Condition;
import analyzer.model.Scenario;
import analyzer.model.mappers.DeviceActionRequestMapper;
import analyzer.service.handlers.snapshot.BaseSnapshotHandler;
import analyzer.storage.ScenarioRepository;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SnapshotProcessor implements Runnable {

    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    private final Map<Class<?>, BaseSnapshotHandler<?>> extractors;
    private final ScenarioRepository scenarioRepository;
    private final HubRouter hubRouter;
    @Value("${analyzer.kafka.topic.snapshots}")
    private String topic;

    public SnapshotProcessor(Consumer<String, SensorsSnapshotAvro> consumer,
                             Set<BaseSnapshotHandler<?>> extractors,
                             ScenarioRepository scenarioRepository,
                             HubRouter hubRouter) {
        this.consumer = consumer;
        this.extractors = extractors.stream()
                .collect(Collectors.toMap(BaseSnapshotHandler::getClass, Function.identity()));
        this.scenarioRepository = scenarioRepository;
        this.hubRouter = hubRouter;
    }


    @Override
    public void run() {
        consumer.subscribe(Collections.singletonList(topic));
        try {
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshotAvro = record.value();
                    try {
                        process(snapshotAvro);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    TopicPartition topicPartition = new TopicPartition(topic, record.partition());
                    offsets.put(topicPartition, new OffsetAndMetadata(record.offset() + 1));

                    consumer.commitSync(offsets);
                }
            }
        } catch (WakeupException e) {
            log.error("Wakeup exception", e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            consumer.commitSync(offsets);
            consumer.close();
        }


    }

    protected void process(SensorsSnapshotAvro snapshotAvro) {
        Map<String, SensorStateAvro> states = snapshotAvro.getSensorsState();
        List<Scenario> scenarios = scenarioRepository.findByHubId(snapshotAvro.getHubId());
        if (scenarios.isEmpty()) {
            return;
        }


        for (Scenario scenario : scenarios) {
            boolean match = scenario.getConditions().entrySet().stream()
                    .allMatch(entry -> {
                        String sensorId = entry.getKey();
                        Condition condition = entry.getValue();
                        SensorStateAvro state = states.get(sensorId);
                        if (state == null) return false;
                        return getCondition(condition, state);
                    });
            if (match) {
                scenario.getActions().forEach((sensorId, action) -> {
                    DeviceActionRequest request = DeviceActionRequestMapper.map(scenario,
                            snapshotAvro.getHubId(),
                            sensorId,
                            action);
                    hubRouter.sendDeviceActionRequest(request);
                });
            }
        }
    }

    private boolean getCondition(Condition condition, SensorStateAvro state) {
        Integer actual = getValue(condition, state.getData());
        Integer expected = condition.getValueInt();

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
        BaseSnapshotHandler<Object> extractor = (BaseSnapshotHandler<Object>) extractors.get(condition.getClass());
        if (extractor == null) {
            throw new RuntimeException(condition.getClass() + " is not supported");
        } else {
            return extractor.extractValue(condition, payload);
        }
    }

    @PreDestroy
    public void destroy() {
        consumer.wakeup();
    }
}
