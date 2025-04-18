package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.GetProductsFilter;
import ru.yandex.practicum.dto.shoppingstore.ProductDtoStore;
import ru.yandex.practicum.dto.shoppingstore.QuantityState;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductDtoStore> getProducts(GetProductsFilter filter);

    ProductDtoStore createProduct(ProductDtoStore newProduct);

    ProductDtoStore updateProduct(ProductDtoStore updatedProduct);

    ProductDtoStore removeProduct(UUID productId);

    ProductDtoStore setQuantityState(UUID productId, QuantityState quantityState);

    ProductDtoStore getProductById(UUID productId);
}
