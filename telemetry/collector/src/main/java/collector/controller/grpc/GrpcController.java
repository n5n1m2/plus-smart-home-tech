package collector.controller.grpc;

import collector.model.hub.handlers.HubEventHandler;
import collector.model.sensors.handlers.SensorEventHandler;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc.CollectorControllerImplBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
public class GrpcController extends CollectorControllerImplBase {
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> events;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEvents;

    public GrpcController(Set<SensorEventHandler> sensorEventHandlers, Set<HubEventHandler> hubEventHandlers) {
        events = sensorEventHandlers.stream()
                .collect(
                        Collectors.toMap(
                                SensorEventHandler::getPayloadCase,
                                Function.identity()
                        )
                );
        hubEvents = hubEventHandlers.stream()
                .collect(
                        Collectors.toMap(
                                HubEventHandler::getPayloadCase,
                                Function.identity()
                        )
                );
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            if (events.containsKey(request.getPayloadCase())) {
                events.get(request.getPayloadCase()).handle(request);
            } else {
                throw new IllegalArgumentException("Unknown sensor event: " + request.getPayloadCase());
            }

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            if (hubEvents.containsKey(request.getPayloadCase())) {
                hubEvents.get(request.getPayloadCase()).handle(request);
            } else {
                throw new IllegalArgumentException("Unknown hub event: " + request.getPayloadCase());
            }

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
