package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.OrderResource;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OrderController implements OrderResource {
    private final OrderService orderService;

    @Override
    public List<OrderDto> getOrdersByUserName(String username, Integer page, Integer size, List<String> sort) {
        log.info("Поступил запрос на получение всех заказов пользователя по имени: {}", username);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.toArray(new String[0])));
        return orderService.getOrdersByUserName(username, pageable);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("Поступил запрос на создание нового заказа");
        return orderService.createNewOrder(request);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        log.info("Поступил запрос на возврат товаров");
        return orderService.returnOrder(request);
    }

    @Override
    public OrderDto payOrder(UUID orderId) {
        log.info("Поступил запрос на оплату заказа");
        return orderService.payOrder(orderId);
    }

    @Override
    public OrderDto confirmPaymentOrder(UUID orderId) {
        log.info("Поступило подтверждение платежа от сервиса оплаты для заказа с id {}", orderId);
        return orderService.confirmPaymentOrder(orderId);
    }

    @Override
    public OrderDto failPaymentOrder(UUID orderId) {
        log.info("Поступила информация от сервиса оплаты о неудачном платеже для заказа с id: {}", orderId);
        return orderService.failPaymentOrder(orderId);
    }

    @Override
    public OrderDto confirmDeliveryOrder(UUID orderId) {
        log.info("Получено подтверждение от службы доставки об успешной траспортировке для заказа с id: {}", orderId);
        return orderService.confirmDeliveryOrder(orderId);
    }

    @Override
    public OrderDto failDeliveryOrder(UUID orderId) {
        log.info("Получено уведомление от службы доставки об ошибке транспортировки для заказа с id: {}", orderId);
        return orderService.failDeliveryOrder(orderId);
    }

    @Override
    public OrderDto acceptDeliveryOrder(UUID orderId) {
        log.info("Получено подтверждение от службы доставки о принятии заявки на перевозку для заказа с id: {}", orderId);
        return orderService.acceptDeliveryOrder(orderId);
    }

    @Override
    public OrderDto completeOrder(UUID orderId) {
        log.info("Получено подтверждение успешного выполнения заказа с id: {}", orderId);
        return orderService.completeOrder(orderId);
    }

    @Override
    public OrderDto calculateTotalPrice(UUID orderId) {
        log.info("Поступил запрос на расчет полной стоимости заказа с id: {}", orderId);
        return orderService.calculateTotalPrice(orderId);
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        log.info("Поступил запрос на расчет стоимости доставки для заказа с id: {}", orderId);
        return orderService.calculateDelivery(orderId);
    }

    @Override
    public OrderDto confirmAssemblyOrder(UUID orderId) {
        log.info("Поступило подтверждение сборки заказа на складе. Id заказа: {}", orderId);
        return orderService.confirmAssemblyOrder(orderId);
    }

    @Override
    public OrderDto failAssemblyOrder(UUID orderId) {
        log.info("Получено уведомление о неуспешной сборке заказа на складе. Id заказа: {}", orderId);
        return orderService.failAssemblyOrder(orderId);
    }
}
