package ru.yandex.practicum.telemetry.collector.service.handlers.hubs;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.constants.TelemetryTopics;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class HubEventHandler {
    private final KafkaEventProducer producer;

    public abstract HubEventProto.PayloadCase getMessageType();

    public abstract void handle(HubEventProto event);

    protected HubEventAvro.Builder getAvroBuilderWithCommonFields(HubEventProto event) {
        Instant timestamp;
        if (event.hasTimestamp()) {
            timestamp = Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
        } else {
            timestamp = Instant.now();
        }
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(timestamp);
    }

    protected void send(HubEventAvro message) {
        String topic = TelemetryTopics.TELEMETRY_HUBS_V1_TOPIC;
        String hubId = message.getHubId();
        Instant timestamp = message.getTimestamp();
        producer.send(message, topic, hubId, timestamp);
    }
}
