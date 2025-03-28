package ru.yandex.practicum.dto.shoppingcart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDto {
    private UUID shoppingCartId;
    private Map<UUID, Long> products;
}
