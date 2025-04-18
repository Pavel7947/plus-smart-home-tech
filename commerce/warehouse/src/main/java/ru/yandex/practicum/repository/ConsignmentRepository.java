package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Consignment;

import java.util.Optional;
import java.util.UUID;

public interface ConsignmentRepository extends JpaRepository<Consignment, UUID> {

    Optional<Consignment> findByOrderId(UUID orderId);

    Boolean existsByOrderId(UUID orderId);
}
