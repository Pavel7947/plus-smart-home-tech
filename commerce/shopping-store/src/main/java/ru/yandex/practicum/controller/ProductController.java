package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.ShoppingStoreResource;
import ru.yandex.practicum.dto.GetProductsFilter;
import ru.yandex.practicum.dto.shoppingstore.ProductCategory;
import ru.yandex.practicum.dto.shoppingstore.ProductDtoStore;
import ru.yandex.practicum.dto.shoppingstore.QuantityState;
import ru.yandex.practicum.service.ProductService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductController implements ShoppingStoreResource {
    private final ProductService productService;

    @Override
    public List<ProductDtoStore> getProducts(ProductCategory category, Set<UUID> productIds,
                                             Integer page, Integer size, List<String> sort) {
        log.info("Поступил запрос на получение товаров");
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.toArray(new String[0])));
        return productService.getProducts(GetProductsFilter.builder()
                .productsIds(productIds)
                .category(category)
                .pageable(pageable)
                .build());
    }

    @Override
    public ProductDtoStore createProduct(ProductDtoStore newProduct) {
        log.info("Поступил запрос на создание товара с телом: {}", newProduct);
        return productService.createProduct(newProduct);
    }

    @Override
    public ProductDtoStore updateProduct(ProductDtoStore updatedProduct) {
        log.info("Поступил запрос на обновление товара с телом: {}", updatedProduct);
        return productService.updateProduct(updatedProduct);
    }

    @Override
    public ProductDtoStore removeProduct(UUID productId) {
        log.info("Поступил запрос на удаление товара по id: {}", productId);
        return productService.removeProduct(productId);
    }


    @Override
    public ProductDtoStore setQuantityState(UUID productId, QuantityState quantityState) {
        log.info("Поступил запрос на изменение статуса товара с id: {}", productId);
        return productService.setQuantityState(productId, quantityState);
    }

    @Override
    public ProductDtoStore getProductById(UUID productId) {
        log.info("Поступил запрос на получение товара по id: {}", productId);
        return productService.getProductById(productId);
    }
}
