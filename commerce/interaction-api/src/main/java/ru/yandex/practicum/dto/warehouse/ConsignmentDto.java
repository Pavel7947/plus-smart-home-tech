package ru.yandex.practicum.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ConsignmentDto {
    private UUID id;
    private UUID orderId;
    private UUID deliveryId;
    private Map<UUID, Long> products;
    private ConsignmentState state;
}
