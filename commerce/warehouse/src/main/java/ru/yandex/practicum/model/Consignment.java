package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.warehouse.ConsignmentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ToString
@Getter
@Setter
@Builder(toBuilder = true)
@Entity
@Table(name = "consignments")
@NoArgsConstructor
@AllArgsConstructor
public class Consignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consignment_id")
    private UUID id;
    @Column(nullable = false)
    private UUID orderId;
    private UUID deliveryId;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "consignments_products", joinColumns = @JoinColumn(name = "consignment_id"))
    @Column(name = "quantity")
    @MapKeyColumn(
            table = "consignments_products",
            name = "product_id"
    )
    @Builder.Default
    private Map<UUID, Long> products = new HashMap<>();
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ConsignmentState state = ConsignmentState.CREATED;
}
