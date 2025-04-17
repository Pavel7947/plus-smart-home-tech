package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderDto> getOrdersByUserName(String username, Pageable pageable);

    OrderDto createNewOrder(CreateNewOrderRequest request);

    OrderDto returnOrder(ProductReturnRequest request);

    OrderDto payOrder(UUID orderId);

    OrderDto confirmPaymentOrder(UUID orderId);

    OrderDto failPaymentOrder(UUID orderId);

    OrderDto confirmDeliveryOrder(UUID orderId);

    OrderDto failDeliveryOrder(UUID orderId);

    OrderDto acceptDeliveryOrder(UUID orderId);

    OrderDto completeOrder(UUID orderId);

    OrderDto calculateTotalPrice(UUID orderId);

    OrderDto calculateDelivery(UUID orderId);

    OrderDto confirmAssemblyOrder(UUID orderId);

    OrderDto failAssemblyOrder(UUID orderId);
}
