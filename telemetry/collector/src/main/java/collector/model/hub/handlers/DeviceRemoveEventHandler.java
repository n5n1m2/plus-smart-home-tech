package collector.model.hub.handlers;

import collector.service.HubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DeviceRemoveEventHandler implements HubEventHandler {

    private final HubService service;

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto hubEvent) {
        DeviceRemovedEventProto e = hubEvent.getDeviceRemoved();

        DeviceRemovedEventAvro avro = DeviceRemovedEventAvro.newBuilder()
                .setId(e.getId())
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
