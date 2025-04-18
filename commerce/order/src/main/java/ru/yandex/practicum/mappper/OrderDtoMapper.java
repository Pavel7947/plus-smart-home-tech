package ru.yandex.practicum.mappper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.model.Order;

import java.util.List;

@UtilityClass
public class OrderDtoMapper {

    public OrderDto mapToOrderDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .products(order.getProducts())
                .shoppingCartId(order.getShoppingCartId())
                .paymentId(order.getPaymentId())
                .deliveryId(order.getDeliveryId())
                .state(order.getState())
                .deliveryWeight(order.getDeliveryWeight())
                .deliveryVolume(order.getDeliveryVolume())
                .fragile(order.getFragile())
                .totalPrice(order.getTotalPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .productPrice(order.getProductPrice())
                .fromAddress(AddressDtoMapper.mapToAddressDto(order.getFromAddress()))
                .toAddress(AddressDtoMapper.mapToAddressDto(order.getToAddress()))
                .build();
    }

    public List<OrderDto> mapToOrderDto(List<Order> orders) {
        return orders.stream().map(OrderDtoMapper::mapToOrderDto).toList();
    }
}
