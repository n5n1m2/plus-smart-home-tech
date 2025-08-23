package collector.model.hub.handlers;

import collector.service.HubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final HubService service;

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto hubEvent) {
        ScenarioRemovedEventProto e = hubEvent.getScenarioRemoved();

        ScenarioRemovedEventAvro avro = ScenarioRemovedEventAvro.newBuilder()
                .setName(e.getName())
                .build();

        service.sendToKafka(HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        hubEvent.getTimestamp().getSeconds(),
                        hubEvent.getTimestamp().getNanos()
                ).toEpochMilli())
                .setPayload(avro)
                .build());
    }
}
