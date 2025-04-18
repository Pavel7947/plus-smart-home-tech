package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.dto.delivery.SendToDeliveryRequest;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.exception.LowQuantityException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ConsignmentDtoMapper;
import ru.yandex.practicum.model.Consignment;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ConsignmentRepository;
import ru.yandex.practicum.repository.ProductRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class ConsignmentServiceImpl implements ConsignmentService {
    private final ProductRepository productRepository;
    private final ConsignmentRepository consignmentRepository;
    private final OrderClient orderClient;
    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    @Override
    public ConsignmentDto cancelBooking(UUID orderId) {
        Consignment consignment = findByOrderId(orderId);
        Map<UUID, Long> quantityMap = consignment.getProducts();
        List<Product> products = productRepository.findAllById(quantityMap.keySet());
        products.forEach(product -> product.setQuantity(product.getQuantity() + quantityMap.get(product.getId())));
        consignmentRepository.delete(consignment);
        return ConsignmentDtoMapper.mapToConsignmentDto(consignment);
    }

    @Override
    public DeliveryParametersDto bookProducts(BookProductsRequest request) {
        UUID orderId = request.getOrderId();
        if (consignmentRepository.existsByOrderId(orderId)) {
            throw new DuplicateException("Бронирование для заказа с id: " + orderId + " уже существует");
        }
        Map<UUID, Long> quantityMap = request.getProducts();
        Set<UUID> productsIds = quantityMap.keySet();
        List<Product> products = productRepository.findAllById(productsIds);
        checkAllProductsFound(products, productsIds);
        checkQuantityProducts(products, quantityMap);
        products.forEach(product -> product.setQuantity(product.getQuantity() - quantityMap.get(product.getId())));
        consignmentRepository.save(Consignment.builder()
                .orderId(orderId)
                .products(quantityMap)
                .build());
        return calculateDeliveryParameters(products);
    }

    @Override
    public ConsignmentDto sendToDelivery(SendToDeliveryRequest request) {
        Consignment consignment = findByOrderId(request.getOrderId());
        consignment.setDeliveryId(request.getDeliveryId());
        consignment.setState(ConsignmentState.SENT_TO_DELIVERY);
        return ConsignmentDtoMapper.mapToConsignmentDto(consignment);
    }

    @Override
    public ConsignmentDto assemblyOrder(UUID orderId) {
        Consignment consignment = findByOrderId(orderId);
        if (consignment.getState() == ConsignmentState.ASSEMBLED) {
            throw new DuplicateException("Заказ уже собран");
        }
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
                orderClient.confirmAssemblyOrder(orderId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.start();
        consignment.setState(ConsignmentState.ASSEMBLED);
        return ConsignmentDtoMapper.mapToConsignmentDto(consignment);
    }

    @Override
    public DeliveryParametersDto checkCart(ShoppingCartDto shoppingCart) {
        Map<UUID, Long> quantityMap = shoppingCart.getProducts();
        Set<UUID> productsIds = quantityMap.keySet();
        List<Product> products = productRepository.findAllById(productsIds);
        checkAllProductsFound(products, productsIds);
        checkQuantityProducts(products, quantityMap);
        return calculateDeliveryParameters(products);
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

    private void checkAllProductsFound(List<Product> products, Set<UUID> requiredProductIds) {
        if (products.size() < requiredProductIds.size()) {
            Set<UUID> findProductIds = products.stream().map(Product::getId).collect(Collectors.toSet());
            String missingProductIds = requiredProductIds.stream().filter(uuid -> !findProductIds.contains(uuid))
                    .map(UUID::toString).collect(Collectors.joining(", "));
            throw new NotFoundException("Некоторые товары на складе не найдены id: " + missingProductIds);
        }
    }

    private void checkQuantityProducts(List<Product> products, Map<UUID, Long> quantityMap) {
        String lowQuantityProductIds = products.stream()
                .filter(product -> product.getQuantity() < quantityMap.get(product.getId())).map(Product::getId)
                .map(UUID::toString).collect(Collectors.joining(", "));
        if (!lowQuantityProductIds.isEmpty()) {
            throw new LowQuantityException("Недостаточное количество некоторых товаров на складе id: " + lowQuantityProductIds);
        }
    }

    private Consignment findByOrderId(UUID orderId) {
        return consignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Бронирование товаров для данного заказа не найдено"));
    }

    private DeliveryParametersDto calculateDeliveryParameters(List<Product> products) {
        double weight = products.stream().map(Product::getWeight).reduce((double) 0, Double::sum);
        double volume = products.stream().map(product -> product.getDepth() * product.getHeight() * product.getWidth())
                .reduce((double) 0, Double::sum);
        Boolean fragile = products.stream().anyMatch(Product::getFragile);
        weight = Math.ceil(weight * 100) / 100.0;
        volume = Math.ceil(volume * 100) / 100.0;
        return DeliveryParametersDto.builder()
                .deliveryWeight(weight)
                .deliveryVolume(volume)
                .fragile(fragile)
                .build();
    }
}
