package ru.yandex.practicum.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryParametersDto {
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
}
