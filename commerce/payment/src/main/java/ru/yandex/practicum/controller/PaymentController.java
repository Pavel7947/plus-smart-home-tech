package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.PaymentResource;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController implements PaymentResource {
    private final PaymentService paymentService;

    @Override
    public PaymentDto cancelPayment(OrderDto orderDto) {
        log.info("Поступил запрос на отмену оплаты");
        return paymentService.cancelPayment(orderDto);
    }

    @Override
    public PaymentDto payOrder(OrderDto orderDto) {
        log.info("Поступил запрос на оплату заказа с телом: {}", orderDto);
        return paymentService.payOrder(orderDto);
    }

    @Override
    public OrderDto calculateTotalCost(OrderDto orderDto) {
        log.info("Поступил запрос на расчет полной стоимости заказа с телом: {}", orderDto);
        return paymentService.calculateTotalCost(orderDto);
    }

    @Override
    public PaymentDto confirmPayment(UUID paymentId) {
        log.info("Поступило подтверждение платежа с id: {} от сервиса оплаты", paymentId);
        return paymentService.confirmPayment(paymentId);
    }

    @Override
    public OrderDto calculateProductCost(OrderDto orderDto) {
        log.info("Поступил запрос на расчет общей стоимости товаров в заказе с телом: {}", orderDto);
        return paymentService.calculateProductCost(orderDto);
    }

    @Override
    public PaymentDto failPayment(UUID paymentId) {
        log.info("Поступила информация от сервиса оплаты о неуспешном платеже c id: {}", paymentId);
        return paymentService.failPayment(paymentId);
    }
}
