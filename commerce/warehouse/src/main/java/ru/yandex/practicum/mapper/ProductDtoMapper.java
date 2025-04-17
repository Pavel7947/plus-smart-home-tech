package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.warehouse.DimensionDto;
import ru.yandex.practicum.dto.warehouse.NewProductWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ProductDtoWarehouse;
import ru.yandex.practicum.model.Product;

@UtilityClass
public class ProductDtoMapper {

    public Product mapToProduct(NewProductWarehouseRequest newProduct) {
        DimensionDto dimension = newProduct.getDimension();
        return Product.builder()
                .id(newProduct.getProductId())
                .width(dimension.getWidth())
                .depth(dimension.getDepth())
                .height(dimension.getHeight())
                .weight(newProduct.getWeight())
                .fragile(newProduct.getFragile())
                .build();
    }

    public ProductDtoWarehouse mapToProductDtoResponse(Product product) {
        return ProductDtoWarehouse.builder()
                .productId(product.getId())
                .weight(product.getWeight())
                .dimension(DimensionDto.builder()
                        .depth(product.getDepth())
                        .height(product.getHeight())
                        .width(product.getWidth()).build())
                .fragile(product.getFragile())
                .quantity(product.getQuantity())
                .build();
    }
}
