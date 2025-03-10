package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.constants.TelemetryTopics;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaConsumerProperties;
import ru.yandex.practicum.telemetry.analyzer.exception.DuplicateException;
import ru.yandex.practicum.telemetry.analyzer.exception.NotFoundException;
import ru.yandex.practicum.telemetry.analyzer.service.handler.HubEventHandler;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final Consumer<String, HubEventAvro> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();
    private final HubEventHandler hubEventHandler;
    private final KafkaConsumerProperties properties;

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(TelemetryTopics.TELEMETRY_HUBS_V1_TOPIC));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records =
                        consumer.poll(Duration.ofSeconds(properties.getPollDurationSeconds().getHubEvent()));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    try {
                        hubEventHandler.handle(record.value());
                    } catch (DuplicateException | NotFoundException e) {
                        log.info("При обработке получено исключение: {} {} ", e.getClass().getSimpleName(), e.getMessage());
                    }
                    currentOffset.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );
                }
                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.warn("Во время фиксации произошла ошибка. Офсет: {}", offsets, exception);
                    }
                });
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от хабов", e);
        } finally {
            try {
                consumer.commitSync(currentOffset);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }
}
