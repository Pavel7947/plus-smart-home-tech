package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

public interface ProductService {

    ProductDtoResponse addNewProduct(NewProductWarehouseRequest productDto);

    BookedProductsDto checkCart(ShoppingCartDto shoppingCart);

    ProductDtoResponse addQuantityProducts(AddProductToWarehouseRequest request);

    AddressDto getAddress();
}
