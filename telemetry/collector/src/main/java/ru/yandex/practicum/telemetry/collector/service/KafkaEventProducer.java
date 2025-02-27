package ru.yandex.practicum.telemetry.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.config.KafkaProducerConfig;

import java.time.Instant;

@Component
@Slf4j
public class KafkaEventProducer {
    private final Producer<String, SpecificRecordBase> producer;

    public KafkaEventProducer(KafkaProducerConfig config) {
        this.producer = new KafkaProducer<>(config.getProducer());
    }

    public void send(SpecificRecordBase event, String topic, String hubId, Instant timestamp) {
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(topic, null, timestamp.toEpochMilli(), hubId, event);
        log.info("Сохраняю событие {}, связанное с хабом {}, в топик {}", event.getClass().getSimpleName(), hubId, topic);
        producer.send(record);
    }
}
