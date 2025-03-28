package ru.yandex.practicum.dto.shoppingcart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChangeProductQuantityRequest {
    private UUID productId;
    private Long newQuantity;
}
