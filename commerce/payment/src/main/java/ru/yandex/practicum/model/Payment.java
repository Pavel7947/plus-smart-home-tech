package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {
    @Id
    @UuidGenerator
    @Column(name = "payment_id")
    private UUID id;
    @Column(nullable = false)
    private Double productCost;
    @Column(nullable = false)
    private UUID orderId;
    @Column(nullable = false)
    private Double deliveryCost;
    @Column(nullable = false)
    private Double totalCost;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentState state = PaymentState.PENDING;
}
