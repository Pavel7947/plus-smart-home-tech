package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ToString
@Getter
@Setter
@Builder(toBuilder = true)
@Entity
@Table(name = "shopping_carts")
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {
    @Id
    @UuidGenerator
    @Column(name = "shopping_cart_id")
    private UUID id;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "products", joinColumns = @JoinColumn(name = "shopping_cart_id"))
    @Column(name = "quantity")
    @MapKeyColumn(
            table = "products",
            name = "product_id"
    )
    @Builder.Default
    private Map<UUID, Long> products = new HashMap<>();
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ShoppingCartState state = ShoppingCartState.ACTIVE;
    @Column(nullable = false)
    private String userName;

}
