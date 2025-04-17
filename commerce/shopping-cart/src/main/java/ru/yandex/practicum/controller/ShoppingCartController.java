package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.ShoppingCartResource;
import ru.yandex.practicum.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartResource {
    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("Поступил запрос на получение корзины от пользователя: {}", username);
        return shoppingCartService.getShoppingCart(username);
    }

    @Override
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Long> products) {
        log.info("Поступил запрос на добавление товаров в корзину {}", products);
        return shoppingCartService.addProductToCart(username, products);
    }

    @Override
    public void deactivateShoppingCart(String username) {
        log.info("Поступил запрос на деактивацию корзины для пользователя {}", username);
        shoppingCartService.deactivateShoppingCart(username);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        log.info("Поступил запрос на удаление товаров из корзины username: {} productIds: {}", username, productIds);
        return shoppingCartService.removeFromShoppingCart(username, productIds);
    }

    @Override
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("Поступил запрос на изменение количества товаров");
        return shoppingCartService.changeQuantity(username, request);
    }
}
