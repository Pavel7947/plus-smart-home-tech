package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.NewDeliveryRequest;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryService {

    DeliveryDto createDelivery(NewDeliveryRequest newDeliveryRequest);

    DeliveryDto confirmDelivery(UUID deliveryId);

    DeliveryDto acceptDelivery(UUID deliveryId);

    DeliveryDto failDelivery(UUID deliveryId);

    OrderDto calculateDeliveryCost(OrderDto orderDto);

    DeliveryDto cancelDelivery(UUID deliveryId);
}
