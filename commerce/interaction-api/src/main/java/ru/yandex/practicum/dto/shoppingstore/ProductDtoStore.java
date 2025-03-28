package ru.yandex.practicum.dto.shoppingstore;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductDtoStore {
    private UUID id;
    private String productName;
    private String description;
    private String imageSrc;
    private ProductState productState;
    private QuantityState quantityState;
    private ProductCategory productCategory;
    private Double price;
}


