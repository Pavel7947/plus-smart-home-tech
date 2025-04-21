package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.DeliveryResource;

@FeignClient(name = "delivery")
public interface DeliveryClient extends DeliveryResource {
}
