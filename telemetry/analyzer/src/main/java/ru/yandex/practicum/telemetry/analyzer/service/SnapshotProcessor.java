package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.constants.TelemetryTopics;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.model.Action;
import ru.yandex.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.model.enums.ActionType;
import ru.yandex.practicum.telemetry.analyzer.model.enums.OperationType;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SnapshotProcessor implements Runnable {
    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();
    private final ScenarioRepository scenarioRepository;

    public SnapshotProcessor(Consumer<String, SensorsSnapshotAvro> consumer,
                             @GrpcClient("hub-router")
                             HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient,
                             ScenarioRepository scenarioRepository) {
        this.consumer = consumer;
        this.hubRouterClient = hubRouterClient;
        this.scenarioRepository = scenarioRepository;
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(TelemetryTopics.TELEMETRY_SNAPSHOTS_V1_TOPIC));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        consumer.poll(Duration.ofSeconds(5));
                if (!records.isEmpty()) {
                    log.info("Поступили в обработку снапшоты кол-во: {}", records.count());
                }
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    for (DeviceActionRequest action : handle(record)) {
                        hubRouterClient.handleDeviceAction(action);
                        log.info("Отправлен запрос действия: {}", action);
                    }
                    currentOffset.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );
                }
                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.warn("Во время фиксации произошла ошибка. Офсет: {}", offsets, exception);
                    }
                });
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                consumer.commitSync(currentOffset);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    @Transactional(readOnly = true)
    private List<DeviceActionRequest> handle(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        SensorsSnapshotAvro newSnapshot = record.value();
        if (snapshots.containsKey(record.key())) {
            SensorsSnapshotAvro sensorsSnapshotAvro = snapshots.get(record.key());
            Instant oldSnapshotTimestamp = sensorsSnapshotAvro.getTimestamp();
            Instant newSnapshotTimestamp = newSnapshot.getTimestamp();
            if (oldSnapshotTimestamp.isAfter(newSnapshotTimestamp)) {
                return List.of();
            }
        }
        snapshots.put(record.key(), newSnapshot);
        List<DeviceActionRequest> actionRequests = new ArrayList<>();
        for (Scenario scenario : scenarioRepository.findByHubId(newSnapshot.getHubId())) {
            Map<String, Condition> conditions = scenario.getConditions();
            if (!newSnapshot.getSensorsState().keySet().containsAll(conditions.keySet())) {
                log.debug("Неполный снапшот от хаба {}: отсутствуют показания некоторых датчиков", newSnapshot.getHubId());
                continue;
            }
            if (checkConditions(conditions, newSnapshot)) {
                Map<String, Action> actions = scenario.getActions();
                for (Map.Entry<String, Action> entry : actions.entrySet()) {
                    Action action = entry.getValue();
                    actionRequests.add(DeviceActionRequest.newBuilder()
                            .setScenarioName(scenario.getName())
                            .setHubId(scenario.getHubId())
                            .setAction(DeviceActionProto.newBuilder()
                                    .setSensorId(entry.getKey())
                                    .setType(mapToActionTypeProto(action.getType()))
                                    .setValue(action.getValue())
                                    .build())
                            .build());
                }
            }
        }
        return actionRequests;
    }

    private boolean checkConditions(Map<String, Condition> conditions, SensorsSnapshotAvro snapshotAvro) {
        for (Map.Entry<String, Condition> entry : conditions.entrySet()) {
            Object data = snapshotAvro.getSensorsState().get(entry.getKey()).getData();
            Condition condition = entry.getValue();
            Integer value = condition.getValue();
            OperationType operationType = condition.getOperation();
            if (!switch (condition.getType()) {
                case TEMPERATURE -> {
                    if (data instanceof TemperatureSensorAvro temperatureState) {
                        yield checkByOperationType(temperatureState.getTemperatureC(), value, operationType);
                    } else {
                        ClimateSensorAvro climateState = (ClimateSensorAvro) data;
                        yield checkByOperationType(climateState.getTemperatureC(), value, operationType);
                    }
                }
                case LUMINOSITY -> {
                    LightSensorAvro lightSensorState = (LightSensorAvro) data;
                    yield checkByOperationType(lightSensorState.getLuminosity(), value, operationType);
                }
                case HUMIDITY -> {
                    ClimateSensorAvro climateSensorState = (ClimateSensorAvro) data;
                    yield checkByOperationType(climateSensorState.getHumidity(), value, operationType);
                }
                case CO2LEVEL -> {
                    ClimateSensorAvro climateSensorState = (ClimateSensorAvro) data;
                    yield checkByOperationType(climateSensorState.getCo2Level(), value, operationType);
                }
                case SWITCH -> {
                    SwitchSensorAvro switchSensorState = (SwitchSensorAvro) data;
                    yield (switchSensorState.getState() ? 1 : 0) == value;
                }
                case MOTION -> {
                    MotionSensorAvro motionSensorState = (MotionSensorAvro) data;
                    yield (motionSensorState.getMotion() ? 1 : 0) == value;
                }
            }) return false;
        }
        return true;
    }

    private boolean checkByOperationType(int currentValue, int conditionValue, OperationType type) {
        return switch (type) {
            case EQUALS -> currentValue == conditionValue;
            case GREATER_THAN -> currentValue > conditionValue;
            case LOWER_THAN -> currentValue < conditionValue;
        };
    }

    private ActionTypeProto mapToActionTypeProto(ActionType actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }
}
