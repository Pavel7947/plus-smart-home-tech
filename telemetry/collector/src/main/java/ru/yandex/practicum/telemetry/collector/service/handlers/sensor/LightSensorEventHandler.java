package ru.yandex.practicum.telemetry.collector.service.handlers.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

@Component
public class LightSensorEventHandler extends SensorEventHandler {

    public LightSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        LightSensorProto payload = event.getLightSensorEvent();
        SensorEventAvro message = getAvroBuilderWithCommonFields(event)
                .setPayload(LightSensorAvro.newBuilder()
                        .setLinkQuality(payload.getLinkQuality())
                        .setLuminosity(payload.getLuminosity())
                        .build())
                .build();
        send(message);
    }
}
