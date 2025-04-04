package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.shoppingstore.ProductDtoStore;
import ru.yandex.practicum.model.Product;

import java.util.List;

@UtilityClass
public class ProductDtoMapper {

    public Product mapToProduct(ProductDtoStore productDtoForStore) {
        return Product.builder()
                .id(productDtoForStore.getId())
                .productName(productDtoForStore.getProductName())
                .description(productDtoForStore.getDescription())
                .productCategory(productDtoForStore.getProductCategory())
                .imageSrc(productDtoForStore.getImageSrc())
                .productState(productDtoForStore.getProductState())
                .quantityState(productDtoForStore.getQuantityState())
                .price(productDtoForStore.getPrice())
                .build();
    }

    public ProductDtoStore mapToProductDto(Product product) {
        return ProductDtoStore.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .productCategory(product.getProductCategory())
                .imageSrc(product.getImageSrc())
                .productState(product.getProductState())
                .quantityState(product.getQuantityState())
                .price(product.getPrice())
                .build();
    }

    public List<ProductDtoStore> mapToProductDto(List<Product> products) {
        return products.stream().map(ProductDtoMapper::mapToProductDto).toList();
    }
}
