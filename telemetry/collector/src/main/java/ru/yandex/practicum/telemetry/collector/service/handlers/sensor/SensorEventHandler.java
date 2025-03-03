package ru.yandex.practicum.telemetry.collector.service.handlers.sensor;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.constants.TelemetryTopics;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class SensorEventHandler {
    private final KafkaEventProducer producer;

    public abstract SensorEventProto.PayloadCase getMessageType();

    public abstract void handle(SensorEventProto event);

    protected SensorEventAvro.Builder getAvroBuilderWithCommonFields(SensorEventProto event) {
        Instant timestamp;
        if (event.hasTimestamp()) {
            timestamp = Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
        } else {
            timestamp = Instant.now();
        }
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setTimestamp(timestamp)
                .setHubId(event.getHubId());
    }

    protected void send(SensorEventAvro message) {
        String topic = TelemetryTopics.TELEMETRY_SENSORS_V1_TOPIC;
        String hubId = message.getHubId();
        Instant timestamp = message.getTimestamp();
        producer.send(message, topic, hubId, timestamp);
    }


}
