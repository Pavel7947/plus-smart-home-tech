package ru.yandex.practicum.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.NewDeliveryRequest;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryResource {

    @PutMapping("/api/v1/delivery")
    DeliveryDto createDelivery(@RequestBody NewDeliveryRequest deliveryDto);

    @PostMapping("/api/v1/delivery/successful")
    DeliveryDto confirmDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/api/v1/delivery/accepted")
    DeliveryDto acceptDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/api/v1/delivery/failed")
    DeliveryDto failDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/api/v1/delivery/canceled")
    DeliveryDto cancelDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/api/v1/delivery/cost")
    OrderDto calculateDeliveryCost(@RequestBody OrderDto orderDto);
}
