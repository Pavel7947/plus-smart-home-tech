package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;

import java.util.Collection;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    Collection<Scenario> findByHubId(String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    boolean existsByHubIdAndName(String hubId, String name);
}