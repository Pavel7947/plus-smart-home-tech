package ru.yandex.practicum.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private UUID paymentId;
    private Double totalCost;
    private Double deliveryCost;
    private Double productCost;
}
