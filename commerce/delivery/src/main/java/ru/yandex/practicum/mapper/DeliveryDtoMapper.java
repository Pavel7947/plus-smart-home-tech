package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.NewDeliveryRequest;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;

@UtilityClass
public class DeliveryDtoMapper {

    public Delivery mapToNewDelivery(NewDeliveryRequest deliveryDto, Address fromAddress, Address toAddress) {
        return Delivery.builder()
                .toAddress(toAddress)
                .fromAddress(fromAddress)
                .orderId(deliveryDto.getOrderId())
                .deliveryWeight(deliveryDto.getDeliveryWeight())
                .deliveryVolume(deliveryDto.getDeliveryVolume())
                .fragile(deliveryDto.getFragile())
                .build();
    }

    public DeliveryDto mapToDeliveryDto(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getId())
                .orderId(delivery.getOrderId())
                .deliveryState(delivery.getDeliveryState())
                .toAddress(AddressDtoMapper.mapToAddressDto(delivery.getToAddress()))
                .fromAddress(AddressDtoMapper.mapToAddressDto(delivery.getFromAddress()))
                .deliveryWeight(delivery.getDeliveryWeight())
                .deliveryVolume(delivery.getDeliveryVolume())
                .fragile(delivery.getFragile())
                .build();
    }
}
