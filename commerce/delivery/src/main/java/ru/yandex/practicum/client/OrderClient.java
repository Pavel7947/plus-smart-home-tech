package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.OrderResource;

@FeignClient(name = "order")
public interface OrderClient extends OrderResource {
}
