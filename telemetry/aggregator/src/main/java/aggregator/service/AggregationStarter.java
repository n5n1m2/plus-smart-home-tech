package aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregationStarter {

    private final Consumer<String, SensorEventAvro> consumer;
    private final AggregatorService service;
    @Value("${topic.sensor}")
    private String topic;

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            new Thread(consumer::wakeup);
        }));

        try {
            consumer.subscribe(List.of(topic));
            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(1000));
                service.handle(records);
            }
        } catch (WakeupException e) {
            log.error("Wakeup exception", e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            service.shutdown();
        }
    }
}
