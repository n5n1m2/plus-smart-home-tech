package collector.service;

import collector.model.hub.DeviceAddedEvent;
import collector.model.hub.DeviceRemoveEvent;
import collector.model.hub.ScenarioAddedEvent;
import collector.model.hub.ScenarioRemovedEvent;
import collector.model.hub.base.BaseHubEvent;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HubService {

    private final Producer<String, SpecificRecordBase> producer;

    @Value("${topic.hubs}")
    private String topic;

    public void sendToKafka(BaseHubEvent baseHubEvent) {
        HubEventAvro event = getRecord(baseHubEvent);
        producer.send(new ProducerRecord<>(topic, event.getHubId(), event));
    }

    private HubEventAvro getRecord(BaseHubEvent hubEvent) {
        SpecificRecord record = switch (hubEvent) {
            case DeviceAddedEvent e -> DeviceAddedEventAvro.newBuilder()
                    .setId(e.getId())
                    .setType(DeviceTypeAvro.valueOf(e.getDeviceType().name()))
                    .build();
            case DeviceRemoveEvent e -> DeviceRemovedEventAvro.newBuilder()
                    .setId(e.getId())
                    .build();
            case ScenarioAddedEvent e -> {
                List<ScenarioConditionAvro> conditionAvro = e.getConditions().stream()
                        .map(obj -> ScenarioConditionAvro.newBuilder()
                                .setSensorId(obj.getSensorId())
                                .setValue(obj.getValue())
                                .setOperation(ConditionOperationAvro.valueOf(obj.getOperation().name()))
                                .setType(ConditionTypeAvro.valueOf(obj.getType().name()))
                                .build())
                        .toList();
                List<DeviceActionAvro> actionAvro = e.getActions().stream()
                        .map(
                                obj -> DeviceActionAvro.newBuilder()
                                        .setSensorId(obj.getSensorId())
                                        .setValue(obj.getValue())
                                        .setType(ActionTypeAvro.valueOf(obj.getType().name()))
                                        .build()
                        )
                        .toList();
                yield ScenarioAddedEventAvro.newBuilder()
                        .setActions(actionAvro)
                        .setConditions(conditionAvro)
                        .setName(e.getName())
                        .build();
            }
            case ScenarioRemovedEvent e -> ScenarioRemovedEventAvro.newBuilder()
                    .setName(e.getName())
                    .build();
            default -> throw new IllegalArgumentException("Unexpected value: " + hubEvent);

        };
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp().toEpochMilli())
                .setPayload(record)
                .build();
    }
}
