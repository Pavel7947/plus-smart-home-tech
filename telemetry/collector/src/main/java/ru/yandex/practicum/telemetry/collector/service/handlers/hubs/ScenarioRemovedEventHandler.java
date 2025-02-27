package ru.yandex.practicum.telemetry.collector.service.handlers.hubs;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

@Component
public class ScenarioRemovedEventHandler extends HubEventHandler {

    public ScenarioRemovedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        ScenarioRemovedEventProto payload = event.getScenarioRemoved();
        HubEventAvro message = getAvroBuilderWithCommonFields(event)
                .setPayload(ScenarioRemovedEventAvro.newBuilder()
                        .setName(payload.getName())
                        .build())
                .build();
        send(message);
    }
}
