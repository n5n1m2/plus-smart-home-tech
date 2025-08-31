package analyzer.service;

import analyzer.service.handlers.hub.BaseHubEventHandler;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    private final Map<Class<?>, BaseHubEventHandler<?>> handlers;
    @Value("${analyzer.kafka.topic.hubs}")
    private String topic;

    public HubEventProcessor(Set<BaseHubEventHandler<?>> handlers, Consumer<String, HubEventAvro> consumer) {
        this.handlers = handlers.stream().collect(Collectors.toMap(
                BaseHubEventHandler::payloadType,
                Function.identity()
        ));
        this.consumer = consumer;
    }


    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList(topic));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro hubEventAvro = record.value();

                    process(hubEventAvro);

                    TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
                    offsets.put(topicPartition, new OffsetAndMetadata(record.offset() + 1));
                }
                consumer.commitSync(offsets);
            }
        } catch (WakeupException e) {
            log.error("Wakeup exception", e);
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
        } finally {
            consumer.commitSync(offsets);
            consumer.close();
        }
    }

    @Transactional
    protected void process(HubEventAvro hubEventAvro) {
        Object payload = hubEventAvro.getPayload();
        System.out.println(hubEventAvro + "\n\n\n");
        BaseHubEventHandler<Object> handler = (BaseHubEventHandler<Object>) handlers.get(payload.getClass());
        if (handler == null) {
            log.error("No handler found for type {}", payload.getClass());
        } else {
            handler.handle(hubEventAvro.getHubId(), payload);
        }
    }

    @PreDestroy
    public void destroy() {
        consumer.wakeup();
    }
}
