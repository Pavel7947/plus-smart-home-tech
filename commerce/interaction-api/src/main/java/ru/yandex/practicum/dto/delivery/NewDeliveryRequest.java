package ru.yandex.practicum.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.dto.warehouse.AddressDto;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class NewDeliveryRequest {
    private AddressDto fromAddress;
    private AddressDto toAddress;
    private UUID orderId;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
}
