package ru.yandex.practicum.telemetry.collector.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.constants.TelemetryTopics;
import ru.yandex.practicum.telemetry.collector.events.hubs.HubEvent;
import ru.yandex.practicum.telemetry.collector.events.sensors.SensorEvent;
import ru.yandex.practicum.telemetry.collector.mapper.EventsAvroMapper;

import java.util.Properties;


@Service
public class KafkaSaveServiceImpl implements SaveService {
    private final Producer<String, SpecificRecordBase> producer;

    public KafkaSaveServiceImpl(@Value("${collector.kafka_bootstrap_server_address}") String serverAddress) {
        producer = initProducer(serverAddress);
    }

    @Override
    public void save(SensorEvent sensorEvent) {
        String topic = TelemetryTopics.TELEMETRY_SENSORS_V1_TOPIC;
        SensorEventAvro message = EventsAvroMapper.toSensorEventAvro(sensorEvent);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, message);
        producer.send(record);
    }

    @Override
    public void save(HubEvent hubEvent) {
        String topic = TelemetryTopics.TELEMETRY_HUBS_V1_TOPIC;
        HubEventAvro message = EventsAvroMapper.toHubEventAvro(hubEvent);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, message);
        producer.send(record);
    }

    private Producer<String, SpecificRecordBase> initProducer(String serverAddress) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "ru.yandex.practicum.telemetry.collector.service.AvroSerializer");
        return new KafkaProducer<>(config);
    }
}
