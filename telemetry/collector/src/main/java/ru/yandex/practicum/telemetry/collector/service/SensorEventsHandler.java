package ru.yandex.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.constants.TelemetryTopics;
import ru.yandex.practicum.telemetry.collector.events.sensors.*;

@Service
@RequiredArgsConstructor
public class SensorEventsHandler implements EventsHandler<SensorEvent> {
    private final Producer<String, SpecificRecordBase> producer;

    @Override
    public void save(SensorEvent sensorEvent) {
        String topic = TelemetryTopics.TELEMETRY_SENSORS_V1_TOPIC;
        SensorEventAvro message = toSensorEventAvro(sensorEvent);
        String hubId = message.getHubId();
        Long timestamp = message.getTimestamp().toEpochMilli();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, null, timestamp, hubId, message);
        producer.send(record);
    }

    private SensorEventAvro toSensorEventAvro(SensorEvent sensorEvent) {
        return SensorEventAvro.newBuilder()
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setId(sensorEvent.getId())
                .setPayload(getPayload(sensorEvent))
                .build();
    }

    private SpecificRecordBase getPayload(SensorEvent sensorEvent) {
        return switch (sensorEvent.getType()) {
            case LIGHT_SENSOR_EVENT -> {
                LightSensorEvent lightSensorEvent = (LightSensorEvent) sensorEvent;
                yield LightSensorAvro.newBuilder()
                        .setLinkQuality(lightSensorEvent.getLinkQuality())
                        .setLuminosity(lightSensorEvent.getLuminosity())
                        .build();
            }
            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) sensorEvent;
                yield ClimateSensorAvro.newBuilder()
                        .setCo2Level(climateSensorEvent.getCo2Level())
                        .setHumidity(climateSensorEvent.getHumidity())
                        .setTemperatureC(climateSensorEvent.getTemperatureC())
                        .build();
            }
            case MOTION_SENSOR_EVENT -> {
                MotionSensorEvent motionSensorEvent = (MotionSensorEvent) sensorEvent;
                yield MotionSensorAvro.newBuilder()
                        .setLinkQuality(motionSensorEvent.getLinkQuality())
                        .setMotion(motionSensorEvent.getMotion())
                        .setVoltage(motionSensorEvent.getVoltage())
                        .build();
            }

            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) sensorEvent;
                yield TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                        .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                        .build();
            }
            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) sensorEvent;
                yield SwitchSensorAvro.newBuilder()
                        .setState(switchSensorEvent.getState())
                        .build();
            }
        };
    }


}
