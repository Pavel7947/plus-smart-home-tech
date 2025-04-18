package ru.yandex.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.shoppingstore.ProductCategory;

import java.util.Set;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class GetProductsFilter {
    private Set<UUID> productsIds;
    private ProductCategory category;
    private Pageable pageable;
}
