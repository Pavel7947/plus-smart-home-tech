package ru.yandex.practicum.telemetry.collector.service.handlers.hubs;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

@Component
public class DeviceAddedEventHandler extends HubEventHandler {

    public DeviceAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        DeviceAddedEventProto payload = event.getDeviceAdded();
        HubEventAvro message = getAvroBuilderWithCommonFields(event)
                .setPayload(DeviceAddedEventAvro.newBuilder()
                        .setId(payload.getId())
                        .setType(mapToDeviceTypeAvro(payload.getType()))
                        .build())
                .build();
        send(message);
    }

    private DeviceTypeAvro mapToDeviceTypeAvro(DeviceTypeProto deviceType) {
        return switch (deviceType) {
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;

            case UNRECOGNIZED -> throw new IllegalArgumentException("Неизвестная константа перечисления: " +
                    DeviceTypeProto.class.getSimpleName());
        };
    }
}
