package analyzer.config.kafka;


import org.apache.avro.Schema;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubEventDeserializer extends AvroDeserializer<HubEventAvro> {
    public HubEventDeserializer(Schema schema) {
        super(schema);
    }

    public HubEventDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}
