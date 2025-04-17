package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.order.OrderState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private UUID id;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "products", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "quantity")
    @MapKeyColumn(
            table = "products",
            name = "product_id"
    )
    @Builder.Default
    private Map<UUID, Long> products = new HashMap<>();
    private UUID shoppingCartId;
    private UUID paymentId;
    private UUID deliveryId;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderState state = OrderState.NEW;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
    private Double totalPrice;
    private Double deliveryPrice;
    private Double productPrice;
    @Column(nullable = false)
    private String userName;
    @ManyToOne
    @JoinColumn(name = "to_address", nullable = false)
    @ToString.Exclude
    private Address toAddress;
    @ManyToOne
    @JoinColumn(name = "from_address", nullable = false)
    @ToString.Exclude
    private Address fromAddress;
}
