package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.SendToDeliveryRequest;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookProductsRequest;
import ru.yandex.practicum.dto.warehouse.ConsignmentDto;
import ru.yandex.practicum.dto.warehouse.DeliveryParametersDto;

import java.util.UUID;

public interface ConsignmentService {

    DeliveryParametersDto bookProducts(BookProductsRequest request);

    ConsignmentDto sendToDelivery(SendToDeliveryRequest request);

    ConsignmentDto assemblyOrder(UUID orderId);

    AddressDto getAddress();

    DeliveryParametersDto checkCart(ShoppingCartDto shoppingCart);

    ConsignmentDto cancelBooking(UUID orderId);
}
