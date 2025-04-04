package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.shoppingstore.ProductCategory;
import ru.yandex.practicum.dto.shoppingstore.ProductDtoStore;
import ru.yandex.practicum.dto.shoppingstore.QuantityState;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductDtoStore> getProductsByType(ProductCategory category, Pageable pageable);

    ProductDtoStore createProduct(ProductDtoStore newProduct);

    ProductDtoStore updateProduct(ProductDtoStore updatedProduct);

    ProductDtoStore removeProduct(UUID productId);

    ProductDtoStore setQuantityState(UUID productId, QuantityState quantityState);

    ProductDtoStore getProductById(UUID productId);
}
