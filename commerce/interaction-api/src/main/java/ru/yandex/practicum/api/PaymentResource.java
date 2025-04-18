package ru.yandex.practicum.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentResource {

    @PostMapping("/api/v1/payment")
    PaymentDto payOrder(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/canceled")
    PaymentDto cancelPayment(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/totalCost")
    OrderDto calculateTotalCost(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/successful")
    PaymentDto confirmPayment(@RequestBody UUID paymentId);

    @PostMapping("/api/v1/payment/productCost")
    OrderDto calculateProductCost(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/failed")
    PaymentDto failPayment(@RequestBody UUID paymentId);
}
