package ru.yandex.practicum.api;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.delivery.SendToDeliveryRequest;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.UUID;

public interface WarehouseResource {

    @PutMapping("/api/v1/warehouse")
    ProductDtoWarehouse addNewProduct(@RequestBody NewProductWarehouseRequest productDto);

    @PostMapping("/api/v1/warehouse/check")
    DeliveryParametersDto checkShoppingCart(@RequestBody ShoppingCartDto shoppingCart);

    @PostMapping("/api/v1/warehouse/add")
    ProductDtoWarehouse addQuantityProducts(@RequestBody AddProductToWarehouseRequest request);

    @PostMapping("/api/v1/warehouse/shipped")
    ConsignmentDto sendToDelivery(@RequestBody SendToDeliveryRequest request);

    @PostMapping("/api/v1/warehouse/assembled")
    ConsignmentDto assemblyOrder(@RequestParam UUID orderId);

    @PostMapping("/api/v1/warehouse/canceled")
    ConsignmentDto cancelBooking(@RequestParam UUID orderId);

    @PostMapping("api/v1/warehouse/booked")
    DeliveryParametersDto bookProducts(@RequestBody BookProductsRequest request);

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getAddress();
}
