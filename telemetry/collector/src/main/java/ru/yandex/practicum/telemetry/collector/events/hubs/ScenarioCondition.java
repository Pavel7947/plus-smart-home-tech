package ru.yandex.practicum.telemetry.collector.events.hubs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.events.hubs.enums.ConditionType;
import ru.yandex.practicum.telemetry.collector.events.hubs.enums.Operation;

@Getter
@Setter
@ToString
public class ScenarioCondition {
    @NotBlank
    private String sensorId;
    @NotNull
    private ConditionType type;
    @NotNull
    private Operation operation;
    @NotNull
    private Integer value;
}
