package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.ShoppingStoreResource;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient extends ShoppingStoreResource {
}
