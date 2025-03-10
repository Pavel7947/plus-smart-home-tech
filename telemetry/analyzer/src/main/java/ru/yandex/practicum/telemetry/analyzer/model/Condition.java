package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.telemetry.analyzer.model.enums.ConditionType;
import ru.yandex.practicum.telemetry.analyzer.model.enums.OperationType;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "conditions")
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated
    private ConditionType type;
    @Enumerated
    private OperationType operation;
    private Integer value;
}
