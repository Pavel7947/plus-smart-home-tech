package ru.yandex.practicum.telemetry.collector.service.handlers.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

@Component
public class ClimateSensorEventHandler extends SensorEventHandler {

    public ClimateSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        ClimateSensorProto payload = event.getClimateSensorEvent();
        SensorEventAvro message = getAvroBuilderWithCommonFields(event)
                .setPayload(ClimateSensorAvro.newBuilder()
                        .setTemperatureC(payload.getTemperatureC())
                        .setCo2Level(payload.getCo2Level())
                        .setHumidity(payload.getHumidity())
                        .build())
                .build();
        send(message);
    }
}
