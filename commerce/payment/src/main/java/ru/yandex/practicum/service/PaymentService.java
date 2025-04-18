package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentService {

    PaymentDto payOrder(OrderDto orderDto);

    OrderDto calculateTotalCost(OrderDto orderDto);

    PaymentDto confirmPayment(UUID paymentId);

    OrderDto calculateProductCost(OrderDto orderDto);

    PaymentDto failPayment(UUID paymentId);

    PaymentDto cancelPayment(OrderDto orderDto);
}
