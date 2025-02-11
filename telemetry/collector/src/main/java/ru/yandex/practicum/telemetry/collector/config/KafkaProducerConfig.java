package ru.yandex.practicum.telemetry.collector.config;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {
    private final Environment env;

    @Bean
    public Producer<String, SpecificRecordBase> getProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("collector.kafka_bootstrap_server_address"));
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, env.getProperty("collector.kafka_key_serializer_class"));
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, env.getProperty("collector.kafka_value_serializer_class"));
        return new KafkaProducer<>(config);
    }
}
