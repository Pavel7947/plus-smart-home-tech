package ru.yandex.practicum.api;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingstore.ProductCategory;
import ru.yandex.practicum.dto.shoppingstore.ProductDtoStore;
import ru.yandex.practicum.dto.shoppingstore.QuantityState;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public interface ShoppingStoreResource {

    @GetMapping("api/v1/shopping-store")
    List<ProductDtoStore> getProducts(@RequestParam(required = false) ProductCategory category,
                                      @RequestParam(required = false) Set<UUID> productIds,
                                      @RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam(defaultValue = "productName") List<String> sort);

    @PutMapping("api/v1/shopping-store")
    ProductDtoStore createProduct(@RequestBody ProductDtoStore newProduct);

    @PostMapping("api/v1/shopping-store")
    ProductDtoStore updateProduct(@RequestBody ProductDtoStore updatedProduct);

    @PostMapping("api/v1/shopping-store/removeProductFromStore")
    ProductDtoStore removeProduct(@RequestBody UUID productId);

    @PostMapping("api/v1/shopping-store/quantityState")
    ProductDtoStore setQuantityState(@RequestParam UUID productId, @RequestParam QuantityState quantityState);

    @GetMapping("api/v1/shopping-store/{productId}")
    ProductDtoStore getProductById(@PathVariable UUID productId);


}
