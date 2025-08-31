package analyzer.service;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Component
@Slf4j
public class HubRouter {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerFutureStub stub;

    public void sendDeviceActionRequest(DeviceActionRequest deviceActionRequest) {
        try {
            stub.handleDeviceAction(deviceActionRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
