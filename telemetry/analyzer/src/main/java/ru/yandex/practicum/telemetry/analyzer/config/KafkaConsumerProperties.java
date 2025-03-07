package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter
@Setter
@ToString
@ConfigurationProperties("analyzer.kafka.consumer")
public class KafkaConsumerProperties {
    Properties sensorSnapshot;
    Properties hubEvent;
}
