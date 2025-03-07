package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.constants.TelemetryTopics;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.exception.DuplicateException;
import ru.yandex.practicum.telemetry.analyzer.exception.NotFoundException;
import ru.yandex.practicum.telemetry.analyzer.model.Action;
import ru.yandex.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;
import ru.yandex.practicum.telemetry.analyzer.model.enums.ActionType;
import ru.yandex.practicum.telemetry.analyzer.model.enums.ConditionType;
import ru.yandex.practicum.telemetry.analyzer.model.enums.OperationType;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final Consumer<String, HubEventAvro> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;


    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(TelemetryTopics.TELEMETRY_HUBS_V1_TOPIC));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records =
                        consumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    handle(record);
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
            log.error("Ошибка во время обработки событий от хабов", e);
        } finally {
            try {
                consumer.commitSync(currentOffset);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private void handle(ConsumerRecord<String, HubEventAvro> record) {
        HubEventAvro event = record.value();
        Object payload = event.getPayload();
        String hubId = event.getHubId();
        try {
            switch (payload) {
                case ScenarioAddedEventAvro eventAvro -> addScenario(eventAvro, hubId);
                case ScenarioRemovedEventAvro eventAvro -> deleteScenario(eventAvro, hubId);
                case DeviceAddedEventAvro eventAvro -> addDevice(eventAvro, hubId);
                default -> deleteDevice((DeviceRemovedEventAvro) payload, hubId);
            }
        } catch (DuplicateException | NotFoundException e) {
            log.info("При обработке получено исключение: {} {} ", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Transactional
    private void addScenario(ScenarioAddedEventAvro eventAvro, String hubId) {
        String name = eventAvro.getName();
        if (scenarioRepository.existsByHubIdAndName(hubId, name)) {
            throw new DuplicateException("Сценарий с названием: " + name + " в пределах  хаба с id: "
                    + hubId + " уже существует");
        }
        checkSensorIds(eventAvro, hubId);
        Map<String, Condition> conditions = eventAvro.getConditions().stream()
                .collect(Collectors.toMap(ScenarioConditionAvro::getSensorId, condition -> Condition.builder()
                        .type(mapToConditionType(condition.getType()))
                        .operation(mapToOperationType(condition.getOperation()))
                        .value(extractValue(condition))
                        .build()));
        Map<String, Action> actions = eventAvro.getActions().stream()
                .collect(Collectors.toMap(DeviceActionAvro::getSensorId, action -> Action.builder()
                        .type(mapToActionType(action.getType()))
                        .value(action.getValue())
                        .build()
                ));
        actionRepository.saveAll(actions.values());
        conditionRepository.saveAll(conditions.values());
        scenarioRepository.save(Scenario.builder()
                .hubId(hubId)
                .name(name)
                .conditions(conditions)
                .actions(actions).build());
    }

    @Transactional
    private void deleteScenario(ScenarioRemovedEventAvro eventAvro, String hubId) {
        String name = eventAvro.getName();
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, name)
                .orElseThrow(() -> new NotFoundException("Сценарий c названием: " + name +
                        " не найден в пределах хаба c id: " + hubId));
        Set<Long> conditionIds = scenario.getConditions().values().stream().map(Condition::getId).collect(Collectors.toSet());
        conditionRepository.deleteAllById(conditionIds);
        Set<Long> actionIds = scenario.getActions().values().stream().map(Action::getId).collect(Collectors.toSet());
        actionRepository.deleteAllById(actionIds);
        scenarioRepository.deleteById(scenario.getId());
    }

    @Transactional
    private void addDevice(DeviceAddedEventAvro eventAvro, String hubId) {
        String sensorId = eventAvro.getId();
        if (sensorRepository.existsById(sensorId)) {
            throw new DuplicateException("Устройство с id: " + sensorId + " уже существует");
        }
        sensorRepository.save(Sensor.builder()
                .id(sensorId)
                .hubId(hubId)
                .build());
    }

    @Transactional
    private void deleteDevice(DeviceRemovedEventAvro eventAvro, String hubId) {
        String sensorId = eventAvro.getId();
        if (!sensorRepository.existsByIdAndHubId(sensorId, hubId)) {
            throw new NotFoundException("Устройства с id: " + sensorId + " в рамках хаба с id: " + hubId +
                    " не существует");
        }
        sensorRepository.deleteById(sensorId);
    }

    private void checkSensorIds(ScenarioAddedEventAvro eventAvro, String hubId) {
        Set<String> ids = eventAvro.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId).collect(Collectors.toSet());
        if (ids.size() < eventAvro.getConditions().size()) {
            throw new DuplicateException("Недопустимо указывать одновременно два условия для одного и того же датчика");
        }
        if (sensorRepository.findByIdInAndHubId(ids, hubId).size() != ids.size()) {
            throw new NotFoundException("id некоторых датчиков указанных в условии сценария " +
                    " не найдены в рамках данного хаба");
        }
        ids = eventAvro.getActions().stream().map(DeviceActionAvro::getSensorId).collect(Collectors.toSet());
        if (ids.size() < eventAvro.getActions().size()) {
            throw new DuplicateException("Недопустимо указывать одновременно два действия для одного и того же устройства");
        }
        if (sensorRepository.findByIdInAndHubId(ids, hubId).size() != ids.size()) {
            throw new DuplicateException("id некоторых устройств указанных в действиях по сценарию " +
                    "не найдены в рамках данного хаба");
        }
    }


    private ConditionType mapToConditionType(ConditionTypeAvro typeAvro) {
        return switch (typeAvro) {
            case MOTION -> ConditionType.MOTION;
            case SWITCH -> ConditionType.SWITCH;
            case CO2LEVEL -> ConditionType.CO2LEVEL;
            case HUMIDITY -> ConditionType.HUMIDITY;
            case LUMINOSITY -> ConditionType.LUMINOSITY;
            case TEMPERATURE -> ConditionType.TEMPERATURE;
        };
    }

    private OperationType mapToOperationType(ConditionOperationAvro typeAvro) {
        return switch (typeAvro) {
            case EQUALS -> OperationType.EQUALS;
            case LOWER_THAN -> OperationType.LOWER_THAN;
            case GREATER_THAN -> OperationType.GREATER_THAN;
        };
    }

    private Integer extractValue(ScenarioConditionAvro conditionAvro) {
        Object valueObj = conditionAvro.getValue();
        if (valueObj instanceof Integer) {
            return (Integer) valueObj;
        }
        return (Boolean) valueObj ? 1 : 0;
    }

    private ActionType mapToActionType(ActionTypeAvro typeAvro) {
        return switch (typeAvro) {
            case INVERSE -> ActionType.INVERSE;
            case ACTIVATE -> ActionType.ACTIVATE;
            case DEACTIVATE -> ActionType.DEACTIVATE;
            case SET_VALUE -> ActionType.SET_VALUE;
        };
    }
}
