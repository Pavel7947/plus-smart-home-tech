package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.WarehouseResource;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.service.ProductService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductController implements WarehouseResource {
    private final ProductService productService;

    @Override
    public ProductDtoResponse addNewProduct(NewProductWarehouseRequest productDto) {
        log.info("Поступил запрос на добавление товара на склад с телом: {}", productDto);
        return productService.addNewProduct(productDto);
    }

    @Override
    public BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCart) {
        log.info("Поступил запрос на проверку корзины с телом: {}", shoppingCart);
        return productService.checkCart(shoppingCart);
    }

    @Override
    public ProductDtoResponse addQuantityProducts(AddProductToWarehouseRequest request) {
        log.info("Поступил запрос на добавление количества продукта с телом: {}", request);
        return productService.addQuantityProducts(request);
    }

    @Override
    public AddressDto getAddress() {
        log.info("Поступил запрос на получение адреса склада");
        return productService.getAddress();
    }
}
