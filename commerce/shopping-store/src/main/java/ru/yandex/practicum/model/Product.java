package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.shoppingstore.ProductCategory;
import ru.yandex.practicum.dto.shoppingstore.ProductState;
import ru.yandex.practicum.dto.shoppingstore.QuantityState;

import java.util.UUID;

@ToString
@Getter
@Setter
@Builder(toBuilder = true)
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", length = 100)
    private UUID id;
    @Column(length = 100, nullable = false)
    private String productName;
    @Column(length = 1200, nullable = false)
    private String description;
    private String imageSrc;
    @Enumerated(value = EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ProductState productState;
    @Enumerated(value = EnumType.STRING)
    @Column(length = 50, nullable = false)
    private QuantityState quantityState;
    @Enumerated(value = EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ProductCategory productCategory;
    @Column(nullable = false)
    private Double price;
}
