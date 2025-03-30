package ru.yandex.practicum.api;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingstore.ProductCategory;
import ru.yandex.practicum.dto.shoppingstore.ProductDtoStore;
import ru.yandex.practicum.dto.shoppingstore.QuantityState;

import java.util.List;
import java.util.UUID;


public interface ShoppingStoreResource {

    @GetMapping("api/v1/shopping-store")
    List<ProductDtoStore> getProductsByType(@RequestParam ProductCategory category, @RequestParam Integer page,
                                            @RequestParam Integer size, @RequestParam List<String> sort);

    @PutMapping("api/v1/shopping-store")
    ProductDtoStore createProduct(@RequestBody ProductDtoStore newProduct);

    @PostMapping("api/v1/shopping-store")
    ProductDtoStore updateProduct(@RequestBody ProductDtoStore updatedProduct);

    @PostMapping("api/v1/shopping-store/removeProductFromStore")
    ProductDtoStore removeProduct(@RequestBody UUID productId);

    @PostMapping("api/v1/shopping-store/quantityState")
    Boolean setQuantityState(@RequestParam UUID productId, @RequestParam QuantityState quantityState);

    @GetMapping("api/v1/shopping-store/{productId}")
    ProductDtoStore getProductById(@PathVariable UUID productId);


}
