package ru.yandex.practicum.telemetry.collector.service.handlers.hubs;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

@Component
public class DeviceRemovedEventHandler extends HubEventHandler {

    public DeviceRemovedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        DeviceRemovedEventProto payload = event.getDeviceRemoved();
        HubEventAvro message = getAvroBuilderWithCommonFields(event)
                .setPayload(DeviceRemovedEventAvro.newBuilder()
                        .setId(payload.getId())
                        .build())
                .build();
        send(message);
    }

}
