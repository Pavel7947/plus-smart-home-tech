package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.DeliveryResource;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.NewDeliveryRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DeliveryController implements DeliveryResource {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto createDelivery(NewDeliveryRequest deliveryDto) {
        log.info("Поступил запрос на создание доставки");
        return deliveryService.createDelivery(deliveryDto);
    }

    @Override
    public DeliveryDto confirmDelivery(UUID deliveryId) {
        log.info("Получено подтверждение от службы доставки об успешной траспортировке");
        return deliveryService.confirmDelivery(deliveryId);
    }

    @Override
    public DeliveryDto acceptDelivery(UUID deliveryId) {
        log.info("Получено подтверждение от службы доставки о принятии заявки на перевозку");
        return deliveryService.acceptDelivery(deliveryId);
    }

    @Override
    public DeliveryDto failDelivery(UUID deliveryId) {
        log.info("Получено уведомление от службы доставки об ошибке транспортировки");
        return deliveryService.failDelivery(deliveryId);
    }

    @Override
    public OrderDto calculateDeliveryCost(OrderDto orderDto) {
        log.info("Поступил запрос на расчет стоимости доставки");
        return deliveryService.calculateDeliveryCost(orderDto);
    }

    @Override
    public DeliveryDto cancelDelivery(UUID deliveryId) {
        log.info("Поступил запрос на отмену доставки с id: {}", deliveryId);
        return deliveryService.cancelDelivery(deliveryId);
    }
}
