package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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
    @Column(name = "product_id", length = 100)
    private UUID id;
    @Column(nullable = false)
    private Boolean fragile;
    @Column(nullable = false)
    private Double width;
    @Column(nullable = false)
    private Double height;
    @Column(nullable = false)
    private Double depth;
    @Column(nullable = false)
    private Double weight;
    @Builder.Default
    @Column(nullable = false)
    private Long quantity = (long) 0;
}
