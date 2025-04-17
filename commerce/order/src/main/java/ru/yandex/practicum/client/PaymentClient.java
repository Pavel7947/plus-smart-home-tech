package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.PaymentResource;

@FeignClient(name = "payment")
public interface PaymentClient extends PaymentResource {
}
