package collector.model.hub.handlers;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandler {

    HubEventProto.PayloadCase getPayloadCase();

    void handle(HubEventProto event);
}
