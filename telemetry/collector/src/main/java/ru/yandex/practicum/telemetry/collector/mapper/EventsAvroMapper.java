package ru.yandex.practicum.telemetry.collector.mapper;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.events.hubs.*;
import ru.yandex.practicum.telemetry.collector.events.hubs.enums.ActionType;
import ru.yandex.practicum.telemetry.collector.events.hubs.enums.ConditionType;
import ru.yandex.practicum.telemetry.collector.events.hubs.enums.DeviceType;
import ru.yandex.practicum.telemetry.collector.events.hubs.enums.Operation;
import ru.yandex.practicum.telemetry.collector.events.sensors.*;

import java.util.List;

@UtilityClass
public class EventsAvroMapper {

    public SensorEventAvro toSensorEventAvro(SensorEvent sensorEvent) {
        return SensorEventAvro.newBuilder()
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setId(sensorEvent.getId())
                .setPayload(getPayload(sensorEvent))
                .build();
    }

    public HubEventAvro toHubEventAvro(HubEvent hubEvent) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(getPayload(hubEvent))
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

    private SpecificRecordBase getPayload(HubEvent hubEvent) {
        return switch (hubEvent.getType()) {
            case DEVICE_ADDED -> {
                DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) hubEvent;
                yield DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAddedEvent.getId())
                        .setType(mapToDeviceTypeAvro(deviceAddedEvent.getDeviceType()))
                        .build();
            }
            case DEVICE_REMOVED -> {
                DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) hubEvent;
                yield DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceRemovedEvent.getId())
                        .build();
            }
            case SCENARIO_REMOVED -> {
                ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) hubEvent;
                yield ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemovedEvent.getName())
                        .build();
            }
            case SCENARIO_ADDED -> {
                ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) hubEvent;
                yield ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAddedEvent.getName())
                        .setConditions(mapToScenarioConditionAvro(scenarioAddedEvent.getConditions()))
                        .setActions(mapToDeviceActionAvro(scenarioAddedEvent.getActions()))
                        .build();
            }
        };
    }

    private DeviceTypeAvro mapToDeviceTypeAvro(DeviceType deviceType) {
        return switch (deviceType) {
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
        };
    }

    private ConditionOperationAvro mapToConditionOperationAvro(Operation operation) {
        return switch (operation) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
        };
    }

    private ConditionTypeAvro mapToConditionTypeAvro(ConditionType conditionType) {
        return switch (conditionType) {
            case MOTION -> ConditionTypeAvro.MOTION;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
        };
    }

    private ActionTypeAvro mapToActionTypeAvro(ActionType actionType) {
        return switch (actionType) {
            case INVERSE -> ActionTypeAvro.INVERSE;
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
        };
    }

    private List<DeviceActionAvro> mapToDeviceActionAvro(List<DeviceAction> deviceActions) {
        return deviceActions.stream().map(deviceAction -> DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(mapToActionTypeAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build()).toList();
    }

    private List<ScenarioConditionAvro> mapToScenarioConditionAvro(List<ScenarioCondition> scenarioConditions) {
        return scenarioConditions.stream().map(scenarioCondition -> ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setOperation(mapToConditionOperationAvro(scenarioCondition.getOperation()))
                .setType(mapToConditionTypeAvro(scenarioCondition.getType()))
                .setValue(scenarioCondition.getValue())
                .build()).toList();
    }
}
