package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@UtilityClass
public class ShoppingCartDtoMapper {

    public ShoppingCartDto mapToShoppingCartDto(ShoppingCart shoppingCart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(shoppingCart.getId())
                .products(shoppingCart.getProducts())
                .build();
    }
}
