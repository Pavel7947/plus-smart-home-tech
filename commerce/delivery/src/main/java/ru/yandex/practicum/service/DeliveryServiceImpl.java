package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.delivery.NewDeliveryRequest;
import ru.yandex.practicum.dto.delivery.SendToDeliveryRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.AddressDtoMapper;
import ru.yandex.practicum.mapper.DeliveryDtoMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.AddressRepository;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final AddressRepository addressRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;
    private static final Double BASE_DELIVERY_COST = 5.0;
    private static final String FIRST_WAREHOUSE_CITY = "ADDRESS_1";
    private static final String SECOND_WAREHOUSE_CITY = "ADDRESS_2";

    @Override
    public DeliveryDto cancelDelivery(UUID deliveryId) {
        Delivery delivery = findById(deliveryId);
        delivery.setDeliveryState(DeliveryState.CANCELLED);
        return DeliveryDtoMapper.mapToDeliveryDto(delivery);
    }

    @Override
    public DeliveryDto createDelivery(NewDeliveryRequest newDeliveryRequest) {
        UUID orderId = newDeliveryRequest.getOrderId();
        if (deliveryRepository.existsByOrderId(orderId)) {
            throw new DuplicateException("Доставка для заказа с id: " + orderId + " уже существует");
        }
        AddressDto toAddress = newDeliveryRequest.getToAddress();
        AddressDto fromAddress = newDeliveryRequest.getFromAddress();
        Delivery newDelivery = DeliveryDtoMapper.mapToNewDelivery(newDeliveryRequest, findExistAddressOrGetNew(toAddress),
                findExistAddressOrGetNew(fromAddress));
        return DeliveryDtoMapper.mapToDeliveryDto(deliveryRepository.save(newDelivery));
    }

    @Override
    public DeliveryDto confirmDelivery(UUID deliveryId) {
        Delivery delivery = findById(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.confirmDeliveryOrder(delivery.getOrderId());
        return DeliveryDtoMapper.mapToDeliveryDto(delivery);
    }

    @Override
    public DeliveryDto acceptDelivery(UUID deliveryId) {
        Delivery delivery = findById(deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        warehouseClient.sendToDelivery(SendToDeliveryRequest.builder()
                .deliveryId(deliveryId)
                .orderId(delivery.getOrderId())
                .build());
        orderClient.acceptDeliveryOrder(delivery.getOrderId());
        return DeliveryDtoMapper.mapToDeliveryDto(delivery);
    }

    @Override
    public DeliveryDto failDelivery(UUID deliveryId) {
        Delivery delivery = findById(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.failDeliveryOrder(delivery.getOrderId());
        return DeliveryDtoMapper.mapToDeliveryDto(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto calculateDeliveryCost(OrderDto orderDto) {
        double totalCost = BASE_DELIVERY_COST;
        AddressDto fromAddress = orderDto.getFromAddress();
        if (fromAddress.getCity().equals(FIRST_WAREHOUSE_CITY)) {
            totalCost += totalCost;
        } else if (fromAddress.getCity().equals(SECOND_WAREHOUSE_CITY)) {
            totalCost += totalCost * 2;
        }
        if (orderDto.getFragile()) {
            totalCost += totalCost * 0.2;
        }
        totalCost += orderDto.getDeliveryWeight() * 0.3;
        totalCost += orderDto.getDeliveryVolume() * 0.2;
        AddressDto toAddress = orderDto.getToAddress();
        if (!toAddress.getStreet().equals(fromAddress.getStreet())) {
            totalCost += totalCost * 0.2;
        }
        orderDto.setDeliveryPrice(totalCost);
        return orderDto;
    }

    private Delivery findById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Доставка с id: " + deliveryId + " не найдена"));
    }

    private Address findExistAddressOrGetNew(AddressDto addressDto) {
        Optional<Address> addressOpt = addressRepository.findByCountryAndCityAndStreetAndHouseAndFlat(addressDto.getCountry(),
                addressDto.getCity(), addressDto.getStreet(), addressDto.getHouse(), addressDto.getFlat());
        return addressOpt.orElseGet(() -> addressRepository.save(AddressDtoMapper.mapToNewAddress(addressDto)));
    }
}
