package ru.yandex.practicum.telemetry.collector.service.handlers.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

@Component
public class MotionSensorEventHandler extends SensorEventHandler {

    public MotionSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        MotionSensorProto payload = event.getMotionSensorEvent();
        SensorEventAvro message = getAvroBuilderWithCommonFields(event)
                .setPayload(MotionSensorAvro.newBuilder()
                        .setLinkQuality(payload.getLinkQuality())
                        .setMotion(payload.getMotion())
                        .setVoltage(payload.getVoltage())
                        .build())
                .build();
        send(message);
    }
}
