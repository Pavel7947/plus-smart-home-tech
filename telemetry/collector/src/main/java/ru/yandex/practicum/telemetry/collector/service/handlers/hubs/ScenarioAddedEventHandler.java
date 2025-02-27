package ru.yandex.practicum.telemetry.collector.service.handlers.hubs;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

import java.util.List;

@Component
public class ScenarioAddedEventHandler extends HubEventHandler {

    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        ScenarioAddedEventProto payload = event.getScenarioAdded();
        HubEventAvro message = getAvroBuilderWithCommonFields(event)
                .setPayload(ScenarioAddedEventAvro.newBuilder()
                        .setName(payload.getName())
                        .setConditions(mapToScenarioConditionAvro(payload.getConditionList()))
                        .setActions(mapToDeviceActionAvro(payload.getActionList()))
                        .build())
                .build();
        send(message);
    }


    private List<ScenarioConditionAvro> mapToScenarioConditionAvro(List<ScenarioConditionProto> scenarioConditions) {
        return scenarioConditions.stream().map(scenarioCondition -> {
            ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                    .setSensorId(scenarioCondition.getSensorId())
                    .setOperation(mapToConditionOperationAvro(scenarioCondition.getOperation()))
                    .setType(mapToConditionTypeAvro(scenarioCondition.getType()));
            switch (scenarioCondition.getValueCase()) {
                case INT_VALUE -> builder.setValue(scenarioCondition.getIntValue());
                case BOOL_VALUE -> builder.setValue(scenarioCondition.getBoolValue());
            }
            return builder.build();
        }).toList();
    }


    private List<DeviceActionAvro> mapToDeviceActionAvro(List<DeviceActionProto> deviceActions) {
        return deviceActions.stream().map(deviceAction -> DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(mapToActionTypeAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build()).toList();
    }


    private ConditionOperationAvro mapToConditionOperationAvro(ConditionOperationProto operation) {
        return switch (operation) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;

            case UNRECOGNIZED -> throw new IllegalArgumentException("Неизвестная константа перечисления: " +
                    ConditionOperationProto.class.getSimpleName());
        };
    }

    private ConditionTypeAvro mapToConditionTypeAvro(ConditionTypeProto conditionType) {
        return switch (conditionType) {
            case MOTION -> ConditionTypeAvro.MOTION;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;

            case UNRECOGNIZED -> throw new IllegalArgumentException("Неизвестная константа перечисления: " +
                    ConditionTypeProto.class.getSimpleName());
        };
    }

    private ActionTypeAvro mapToActionTypeAvro(ActionTypeProto actionType) {
        return switch (actionType) {
            case INVERSE -> ActionTypeAvro.INVERSE;
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;

            case UNRECOGNIZED -> throw new IllegalArgumentException("Неизвестная константа перечисления: " +
                    ActionTypeProto.class.getSimpleName());
        };
    }

}
