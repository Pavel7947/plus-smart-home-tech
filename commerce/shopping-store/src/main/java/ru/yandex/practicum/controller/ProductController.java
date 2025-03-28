package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.ShoppingStoreResource;
import ru.yandex.practicum.dto.shoppingstore.ProductCategory;
import ru.yandex.practicum.dto.shoppingstore.ProductDtoStore;
import ru.yandex.practicum.dto.shoppingstore.QuantityState;
import ru.yandex.practicum.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductController implements ShoppingStoreResource {
    private final ProductService productService;

    @Override
    public List<ProductDtoStore> getProductsByType(ProductCategory category, Integer page, Integer size, List<String> sort) {
        log.info("Поступил запрос на получение товаров по категории {}", category);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.toArray(new String[0])));
        return productService.getProductsByType(category, pageable);
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
    public Boolean removeProduct(UUID productId) {
        log.info("Поступил запрос на удаление товара по id: {}", productId);
        return productService.removeProduct(productId);
    }


    @Override
    public Boolean setQuantityState(UUID productId, QuantityState quantityState) {
        log.info("Поступил запрос на изменение статуса товара с id: {}", productId);
        return productService.setQuantityState(productId, quantityState);
    }

    @Override
    public ProductDtoStore getProductById(UUID productId) {
        log.info("Поступил запрос на получение товара по id: {}", productId);
        return productService.getProductById(productId);
    }
}
