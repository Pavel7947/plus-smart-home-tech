package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.warehouse.ConsignmentDto;
import ru.yandex.practicum.model.Consignment;

@UtilityClass
public class ConsignmentDtoMapper {

    public ConsignmentDto mapToConsignmentDto(Consignment consignment) {
        return ConsignmentDto.builder()
                .id(consignment.getId())
                .orderId(consignment.getOrderId())
                .deliveryId(consignment.getDeliveryId())
                .products(consignment.getProducts())
                .build();
    }
}
