package aggregator.kafka;

import org.apache.avro.Schema;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorEventDeserializer extends AvroDeserializer<SensorEventAvro> {
    public SensorEventDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }

    public SensorEventDeserializer(Schema schema) {
        super(schema);
    }
}
