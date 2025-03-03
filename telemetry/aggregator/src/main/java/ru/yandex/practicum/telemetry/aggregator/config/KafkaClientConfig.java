package ru.yandex.practicum.telemetry.aggregator.config;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Configuration
@RequiredArgsConstructor
public class KafkaClientConfig {
    private final KafkaClientProperties properties;

    @Bean
    public Producer<String, SpecificRecordBase> getProducer() {
        return new KafkaProducer<>(properties.getProducer());
    }

    @Bean
    public Consumer<String, SensorEventAvro> getConsumer() {
        return new KafkaConsumer<>(properties.getConsumer());
    }
}
