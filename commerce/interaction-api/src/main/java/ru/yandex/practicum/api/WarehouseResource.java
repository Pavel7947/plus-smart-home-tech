package ru.yandex.practicum.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

public interface WarehouseResource {

    @PutMapping("/api/v1/warehouse")
    ProductDtoResponse addNewProduct(@RequestBody NewProductWarehouseRequest productDto);

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkShoppingCart(@RequestBody ShoppingCartDto shoppingCart);

    @PostMapping("/api/v1/warehouse/add")
    ProductDtoResponse addQuantityProducts(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getAddress();
}
