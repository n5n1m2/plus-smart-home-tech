package analyzer.config.kafka;

import org.apache.avro.Schema;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SensorSnapshotDeserializer extends AvroDeserializer<SensorsSnapshotAvro> {
    public SensorSnapshotDeserializer(Schema schema) {
        super(schema);
    }

    public SensorSnapshotDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
