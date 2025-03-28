package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.exception.BadRequestException;
import ru.yandex.practicum.exception.DeactivateShoppingCartException;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.mapper.ShoppingCartDtoMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.ShoppingCartState;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        Optional<ShoppingCart> shoppingCartOpt = shoppingCartRepository.findByUserName(username);
        if (shoppingCartOpt.isEmpty() || shoppingCartOpt.get().getProducts().isEmpty()) {
            throw new BadRequestException("Нельзя изменять количество товара в пустой корзине");
        }
        ShoppingCart shoppingCart = shoppingCartOpt.get();
        checkActive(shoppingCart);
        if (!shoppingCart.getProducts().containsKey(request.getProductId())) {
            throw new BadRequestException("Товар с id: " + request.getProductId() + " в корзине не найден");
        }
        checkQuantityProducts(shoppingCart.getId(), Map.of(request.getProductId(), request.getNewQuantity()));
        shoppingCart.getProducts().put(request.getProductId(), request.getNewQuantity());
        return ShoppingCartDtoMapper.mapToShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        return ShoppingCartDtoMapper.mapToShoppingCartDto(shoppingCartRepository.findByUserName(username)
                .orElseGet(() -> createNewShoppingCart(username)));
    }

    @Override
    public void deactivateShoppingCart(String username) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserName(username)
                .orElseGet(() -> createNewShoppingCart(username));
        shoppingCart.setState(ShoppingCartState.DEACTIVATE);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        Optional<ShoppingCart> shoppingCartOpt = shoppingCartRepository.findByUserName(username);
        if (shoppingCartOpt.isEmpty() || shoppingCartOpt.get().getProducts().isEmpty()) {
            throw new BadRequestException("Нельзя удалять товары из пустой корзины");
        }
        ShoppingCart shoppingCart = shoppingCartOpt.get();
        checkActive(shoppingCart);
        Map<UUID, Long> products = shoppingCart.getProducts();
        String errorMessage = productIds.stream().filter(uuid -> !products.containsKey(uuid)).map(UUID::toString)
                .collect(Collectors.joining(", "));
        if (!errorMessage.isEmpty()) {
            throw new BadRequestException("Некоторые искомые товары в корзине отсутствуют id: " + errorMessage);
        }
        productIds.forEach(uuid -> products.remove(uuid));
        return ShoppingCartDtoMapper.mapToShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Long> products) {
        Optional<ShoppingCart> shoppingCartOpt = shoppingCartRepository
                .findByUserName(username);
        ShoppingCart shoppingCart;
        if (shoppingCartOpt.isPresent()) {
            shoppingCart = shoppingCartOpt.get();
            checkActive(shoppingCart);
            String message = products.keySet().stream().filter(shoppingCart.getProducts()::containsKey)
                    .map(UUID::toString).collect(Collectors.joining(", "));
            if (!message.isEmpty()) {
                throw new DuplicateException("Некоторые товары уже добавлены в корзину id: " + message);
            }
        } else {
            shoppingCart = createNewShoppingCart(username);
        }
        checkQuantityProducts(shoppingCart.getId(), products);
        shoppingCart.getProducts().putAll(products);
        return ShoppingCartDtoMapper.mapToShoppingCartDto(shoppingCart);
    }

    private ShoppingCart createNewShoppingCart(String username) {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userName(username)
                .build();
        shoppingCartRepository.save(shoppingCart);
        return shoppingCart;
    }

    private void checkActive(ShoppingCart shoppingCart) {
        if (shoppingCart.getState() == ShoppingCartState.DEACTIVATE) {
            throw new DeactivateShoppingCartException("Нельзя изменять деактивированную корзину");
        }
    }

    private void checkQuantityProducts(UUID shoppingCartId, Map<UUID, Long> products) {
        warehouseClient.checkShoppingCart(ShoppingCartDto.builder()
                .shoppingCartId(shoppingCartId)
                .products(products)
                .build());
    }


}
