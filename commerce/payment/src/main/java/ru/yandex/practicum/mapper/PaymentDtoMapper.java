package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.model.Payment;

@UtilityClass
public class PaymentDtoMapper {

    public PaymentDto mapToPaymentDto(Payment payment) {
        return PaymentDto.builder()
                .paymentId(payment.getId())
                .deliveryCost(payment.getDeliveryCost())
                .totalCost(payment.getTotalCost())
                .productCost(payment.getProductCost())
                .build();
    }
}
