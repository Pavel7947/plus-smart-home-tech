package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.exception.LowQuantityException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ProductDtoMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    @Override
    @Transactional
    public ProductDtoResponse addNewProduct(NewProductWarehouseRequest productDto) {
        if (productRepository.existsById(productDto.getProductId())) {
            throw new DuplicateException("Продукт с id: " + productDto.getProductId() + " уже существует");
        }
        return ProductDtoMapper.mapToProductDtoResponse(productRepository.save(ProductDtoMapper.mapToProduct(productDto)));
    }

    @Override
    @Transactional
    public BookedProductsDto checkCart(ShoppingCartDto shoppingCart) {
        Map<UUID, Long> quantityMap = shoppingCart.getProducts();
        Set<UUID> productsIds = quantityMap.keySet();
        List<Product> products = productRepository.findAllById(productsIds);
        if (products.size() < productsIds.size()) {
            Set<UUID> findProductIds = products.stream().map(Product::getId).collect(Collectors.toSet());
            String missingProductIds = productsIds.stream().filter(uuid -> !findProductIds.contains(uuid))
                    .map(UUID::toString).collect(Collectors.joining(", "));
            throw new NotFoundException("Некоторые товары на складе не найдены id: " + missingProductIds);
        }
        String lowQuantityProductIds = products.stream()
                .filter(product -> product.getQuantity() < quantityMap.get(product.getId())).map(Product::getId)
                .map(UUID::toString).collect(Collectors.joining(", "));
        if (!lowQuantityProductIds.isEmpty()) {
            throw new LowQuantityException("Недостаточное количество некоторых товаров на складе id: " + lowQuantityProductIds);
        }
        Double weight = products.stream().map(Product::getWeight).reduce((double) 0, Double::sum);
        Double volume = products.stream().map(product -> product.getDepth() * product.getHeight() * product.getWidth())
                .reduce((double) 0, Double::sum);
        Boolean fragile = products.stream().anyMatch(Product::getFragile);
        return BookedProductsDto.builder()
                .deliveryWeight(weight)
                .deliveryVolume(volume)
                .fragile(fragile)
                .build();
    }

    @Override
    @Transactional
    public ProductDtoResponse addQuantityProducts(AddProductToWarehouseRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Товар c id: " + request.getProductId() + " на складе не найден"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        return ProductDtoMapper.mapToProductDtoResponse(product);
    }

    @Override
    public AddressDto getAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }
}
