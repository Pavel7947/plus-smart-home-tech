package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.WarehouseResource;

@FeignClient(name = "warehouse")
public interface WarehouseClient extends WarehouseResource {
}
