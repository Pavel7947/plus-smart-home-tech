package ru.yandex.practicum.api;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderResource {

    @GetMapping("/api/v1/order")
    List<OrderDto> getOrdersByUserName(@RequestParam String username, @RequestParam Integer page,
                                       @RequestParam Integer size, @RequestParam List<String> sort);

    @PutMapping("/api/v1/order")
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest request);

    @PostMapping("/api/v1/order/return")
    OrderDto returnOrder(@RequestBody ProductReturnRequest request);

    @PostMapping("/api/v1/order/payment")
    OrderDto payOrder(@RequestParam UUID orderId);

    @PostMapping("/api/v1/order/payment/successful")
    OrderDto confirmPaymentOrder(@RequestParam UUID orderId);

    @PostMapping("/api/v1/order/payment/failed")
    OrderDto failPaymentOrder(@RequestParam UUID orderId);

    @PostMapping("/api/v1/order/delivery/successful")
    OrderDto confirmDeliveryOrder(@RequestParam UUID orderId);

    @PostMapping("/api/v1/order/delivery/failed")
    OrderDto failDeliveryOrder(@RequestParam UUID orderId);

    @PostMapping("/api/v1/order/delivery/accepted")
    OrderDto acceptDeliveryOrder(@RequestParam UUID orderId);

    @PostMapping("/api/v1/order/completed")
    OrderDto completeOrder(@RequestParam UUID orderId);

    @PostMapping("/api/v1/order/calculate/total")
    OrderDto calculateTotalPrice(@RequestParam UUID orderId);

    @PostMapping("/api/v1/order/calculate/delivery")
    OrderDto calculateDelivery(@RequestParam UUID orderId);

    @PostMapping("/api/v1/order/assembly/successful")
    OrderDto confirmAssemblyOrder(@RequestParam UUID orderId);

    @PostMapping("/api/v1/order/assembly/failed")
    OrderDto failAssemblyOrder(@RequestParam UUID orderId);
}
