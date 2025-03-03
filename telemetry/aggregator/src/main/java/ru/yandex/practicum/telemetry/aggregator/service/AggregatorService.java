package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.constants.TelemetryTopics;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregatorService implements CommandLineRunner {
    private final Consumer<String, SensorEventAvro> consumer;
    private final Producer<String, SpecificRecordBase> producer;
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();
    private final Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();

    @Override
    public void run(String... args) {
        try {
            consumer.subscribe(List.of(TelemetryTopics.TELEMETRY_SENSORS_V1_TOPIC));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            while (true) {
                ConsumerRecords<String, SensorEventAvro> records =
                        consumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    Optional<SensorsSnapshotAvro> snapshotOpt = updateState(record);
                    if (snapshotOpt.isPresent()) {
                        SensorsSnapshotAvro snapshot = snapshotOpt.get();
                        send(snapshot);
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
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                consumer.commitSync(currentOffset);
                producer.flush();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    private void send(SensorsSnapshotAvro snapshot) {
        String id = snapshot.getHubId();
        Long timestamp = snapshot.getTimestamp().toEpochMilli();
        String topic = TelemetryTopics.TELEMETRY_SNAPSHOTS_V1_TOPIC;
        ProducerRecord<String, SpecificRecordBase> producerRecord =
                new ProducerRecord<>(topic, null, timestamp, id, snapshot);
        log.info("Сохраняю снапшот {}, связанный с хабом {}, в топик {}", snapshot, id, topic);
        producer.send(producerRecord);
    }

    /**
     * Обновляет состояние существующего снапшота в коллекции или добавляет новый.
     *
     * @param record Запись c событием SensorEventAvro
     * @return {@code Optional.empty()} - Если переданная запись не обновила коллекцию снапшотов.
     * В обратном случае возвращается обновленный снапшот в обертке: {@code Optional<SensorsSnapshotAvro>}.
     */
    private Optional<SensorsSnapshotAvro> updateState(ConsumerRecord<String, SensorEventAvro> record) {
        SensorsSnapshotAvro snapshot;
        if (snapshots.containsKey(record.key())) {
            snapshot = snapshots.get(record.key());
            Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
            SensorEventAvro event = record.value();
            String eventId = event.getId();
            if (sensorsState.containsKey(eventId)) {
                SensorStateAvro oldState = sensorsState.get(eventId);
                if (oldState.getTimestamp().isAfter(event.getTimestamp())) {
                    return Optional.empty();
                }
                if (oldState.getData().equals(event.getPayload())) {
                    return Optional.empty();
                }
            }
            sensorsState.put(event.getId(), SensorStateAvro.newBuilder()
                    .setTimestamp(event.getTimestamp())
                    .setData(event)
                    .build()
            );
            snapshot.setTimestamp(event.getTimestamp());
        } else {
            SensorEventAvro event = record.value();
            Map<String, SensorStateAvro> sensorsState = new HashMap<>();
            sensorsState.put(event.getId(), SensorStateAvro.newBuilder()
                    .setTimestamp(event.getTimestamp())
                    .setData(event)
                    .build());
            snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(record.key())
                    .setTimestamp(event.getTimestamp())
                    .setSensorsState(sensorsState)
                    .build();
        }
        snapshots.put(record.key(), snapshot);
        return Optional.of(snapshot);
    }
}