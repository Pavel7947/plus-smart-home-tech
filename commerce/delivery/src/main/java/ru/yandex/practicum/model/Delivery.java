package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.dto.delivery.DeliveryState;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deliveries")
public class Delivery {
    @Id
    @UuidGenerator
    @Column(name = "delivery_id")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "from_address", nullable = false)
    private Address fromAddress;
    @ManyToOne
    @JoinColumn(name = "to_address", nullable = false)
    private Address toAddress;
    @Column(nullable = false)
    private UUID orderId;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DeliveryState deliveryState = DeliveryState.CREATED;
    @Column(nullable = false)
    private Double deliveryWeight;
    @Column(nullable = false)
    private Double deliveryVolume;
    @Column(nullable = false)
    private Boolean fragile;
}
