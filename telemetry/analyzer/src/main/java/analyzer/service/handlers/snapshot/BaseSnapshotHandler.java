package analyzer.service.handlers.snapshot;

import analyzer.model.Condition;

public interface BaseSnapshotHandler<T> {
    Class<T> payloadType();
    Integer extractValue(Condition condition, T sensorAvro);
}
