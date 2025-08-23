package collector.model.hub.handlers;

import collector.service.HubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final HubService hubService;

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto hubEvent) {
        ScenarioAddedEventProto e = hubEvent.getScenarioAdded();

        List<ScenarioConditionAvro> conditionAvro = e.getConditionList().stream()
                .map(obj -> {

                    Object val = switch (obj.getValueCase()) {
                        case BOOL_VALUE -> obj.getBoolValue();
                        case INT_VALUE -> obj.getIntValue();
                        case VALUE_NOT_SET -> null;
                    };
                    return ScenarioConditionAvro.newBuilder()
                            .setValue(val)
                            .setOperation(ConditionOperationAvro.valueOf(obj.getOperation().name()))
                            .setType(ConditionTypeAvro.valueOf(obj.getType().name()))
                            .build();
                })
                .toList();

        List<DeviceActionAvro> actionAvro = e.getActionList().stream()
                .map(
                        obj -> DeviceActionAvro.newBuilder()
                                .setSensorId(obj.getSensorId())
                                .setValue(obj.getValue())
                                .setType(ActionTypeAvro.valueOf(obj.getType().name()))
                                .build()
                )
                .toList();

        ScenarioAddedEventAvro avro = ScenarioAddedEventAvro.newBuilder()
                .setName(e.getName())
                .setConditions(conditionAvro)
                .setActions(actionAvro)
                .build();

        hubService.sendToKafka(HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        hubEvent.getTimestamp().getSeconds(),
                        hubEvent.getTimestamp().getNanos()
                ).toEpochMilli())
                .setPayload(avro)
                .build());

    }
}
