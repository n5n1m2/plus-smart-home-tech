package analyzer.service.handlers.hub;

public interface BaseHubEventHandler<T> {
    Class<T> payloadType();
    void handle(String hubId, T payload);
}
