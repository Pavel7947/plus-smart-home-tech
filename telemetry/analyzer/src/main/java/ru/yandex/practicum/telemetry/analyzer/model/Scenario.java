package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "scenarios",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hub_id", "name"}))
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "hub_id")
    private String hubId;
    private String name;
    @OneToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinTable(
            name = "scenario_conditions",
            joinColumns = @JoinColumn(name = "scenario_id"),
            inverseJoinColumns = @JoinColumn(name = "condition_id"))
    @MapKeyColumn(
            table = "scenario_conditions",
            name = "sensor_id"
    )
    private Map<String, Condition> conditions;
    @OneToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinTable(
            name = "scenario_actions",
            joinColumns = @JoinColumn(name = "scenario_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id"))
    @MapKeyColumn(
            table = "scenario_actions",
            name = "sensor_id"
    )
    private Map<String, Action> actions;

}
