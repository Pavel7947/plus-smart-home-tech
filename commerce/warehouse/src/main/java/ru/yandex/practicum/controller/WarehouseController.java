package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.WarehouseResource;
import ru.yandex.practicum.dto.delivery.SendToDeliveryRequest;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.service.ConsignmentService;
import ru.yandex.practicum.service.ProductService;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class WarehouseController implements WarehouseResource {
    private final ProductService productService;
    private final ConsignmentService consignmentService;

    @Override
    public ConsignmentDto cancelBooking(UUID orderId) {
        log.info("Поступил запрос на отмену бронирования продукта");
        return consignmentService.cancelBooking(orderId);
    }

    @Override
    public ConsignmentDto sendToDelivery(SendToDeliveryRequest request) {
        log.info("Поступил запрос на передачу товаров в доставку с телом: {}", request);
        return consignmentService.sendToDelivery(request);
    }

    @Override
    public ConsignmentDto assemblyOrder(UUID orderId) {
        log.info("Поступил запрос на сборку заказа с id: {}", orderId);
        return consignmentService.assemblyOrder(orderId);
    }

    @Override
    public DeliveryParametersDto bookProducts(BookProductsRequest request) {
        log.info("Поступил запрос на бронирование товаров при оформлении заказа c телом: {}", request);
        return consignmentService.bookProducts(request);
    }

    @Override
    public ProductDtoWarehouse addNewProduct(NewProductWarehouseRequest productDto) {
        log.info("Поступил запрос на добавление товара на склад с телом: {}", productDto);
        return productService.addNewProduct(productDto);
    }

    @Override
    public DeliveryParametersDto checkShoppingCart(ShoppingCartDto shoppingCart) {
        log.info("Поступил запрос на проверку корзины с телом: {}", shoppingCart);
        return consignmentService.checkCart(shoppingCart);
    }

    @Override
    public ProductDtoWarehouse addQuantityProducts(AddProductToWarehouseRequest request) {
        log.info("Поступил запрос на добавление количества продукта с телом: {}", request);
        return productService.addQuantityProducts(request);
    }

    @Override
    public AddressDto getAddress() {
        log.info("Поступил запрос на получение адреса склада");
        return consignmentService.getAddress();
    }
}
