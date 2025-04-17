package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.NewDeliveryRequest;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookProductsRequest;
import ru.yandex.practicum.dto.warehouse.DeliveryParametersDto;
import ru.yandex.practicum.exception.BadRequestException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mappper.AddressDtoMapper;
import ru.yandex.practicum.mappper.OrderDtoMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.AddressRepository;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;
    private final AddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserName(String username, Pageable pageable) {
        return OrderDtoMapper.mapToOrderDto(orderRepository.findAllByUserName(username, pageable));
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        ShoppingCartDto shoppingCart = request.getShoppingCart();
        Map<UUID, Long> products = shoppingCart.getProducts();
        Order order = orderRepository.save(Order.builder()
                .products(products)
                .shoppingCartId(shoppingCart.getShoppingCartId())
                .toAddress(findExistAddressOrGetNew(request.getDeliveryAddress()))
                .fromAddress(findExistAddressOrGetNew(warehouseClient.getAddress()))
                .build());
        DeliveryParametersDto deliveryParameters = bookProducts(order);
        order.setDeliveryVolume(deliveryParameters.getDeliveryVolume());
        order.setFragile(deliveryParameters.getFragile());
        order.setDeliveryWeight(deliveryParameters.getDeliveryWeight());
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        Order order = findById(request.getOrderId());
        OrderState state = order.getState();
        switch (state) {
            case ASSEMBLED:
                deliveryClient.cancelDelivery(order.getDeliveryId());
            case PAID, ON_PAYMENT:
                paymentClient.cancelPayment(OrderDtoMapper.mapToOrderDto(order));
            case NEW:
                warehouseClient.cancelBooking(order.getId());
                order.setState(OrderState.CANCELED);
                break;
            /* Во всех предыдущих состояниях заказ еще не покинул склад
               и транспортная компания за него не взялась поэтому можем смело его отменить */
            case ON_DELIVERY, DELIVERED, COMPLETED:
                // Здесь мы должны отправить уведомление администратору о возврате (Пока не реализовано)
                break;
            case CANCELED:
                throw new BadRequestException("Заказ уже отменен");
            default:
                throw new BadRequestException("Нельзя отменить неуспешный заказ");
        }
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto payOrder(UUID orderId) {
        Order order = findById(orderId);
        OrderState orderState = order.getState();
        if (orderState != OrderState.NEW && orderState != OrderState.PAYMENT_FAILED) {
            throw new BadRequestException("Нельзя оплатить ранее оплаченный заказ");
        }
        PaymentDto paymentDto = paymentClient.payOrder(OrderDtoMapper.mapToOrderDto(order));
        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.ON_PAYMENT);
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto confirmPaymentOrder(UUID orderId) {
        Order order = findById(orderId);
        order.setState(OrderState.PAID);
        warehouseClient.assemblyOrder(orderId);
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto failPaymentOrder(UUID orderId) {
        Order order = findById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto confirmDeliveryOrder(UUID orderId) {
        Order order = findById(orderId);
        order.setState(OrderState.DELIVERED);
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto failDeliveryOrder(UUID orderId) {
        Order order = findById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto acceptDeliveryOrder(UUID orderId) {
        Order order = findById(orderId);
        order.setState(OrderState.ON_DELIVERY);
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto completeOrder(UUID orderId) {
        Order order = findById(orderId);
        order.setState(OrderState.COMPLETED);
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto calculateTotalPrice(UUID orderId) {
        Order order = findById(orderId);
        OrderDto orderDto = paymentClient.calculateProductCost(OrderDtoMapper.mapToOrderDto(order));
        orderDto = paymentClient.calculateTotalCost(orderDto);
        order.setProductPrice(orderDto.getProductPrice());
        order.setTotalPrice(orderDto.getTotalPrice());
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        Order order = findById(orderId);
        OrderDto orderDto = deliveryClient.calculateDeliveryCost(OrderDtoMapper.mapToOrderDto(order));
        order.setDeliveryPrice(orderDto.getDeliveryPrice());
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto confirmAssemblyOrder(UUID orderId) {
        Order order = findById(orderId);
        order.setState(OrderState.ASSEMBLED);
        DeliveryDto deliveryDto = deliveryClient.createDelivery(NewDeliveryRequest.builder()
                .orderId(order.getId())
                .deliveryWeight(order.getDeliveryWeight())
                .deliveryVolume(order.getDeliveryVolume())
                .fragile(order.getFragile())
                .fromAddress(AddressDtoMapper.mapToAddressDto(order.getFromAddress()))
                .toAddress(AddressDtoMapper.mapToAddressDto(order.getToAddress()))
                .build());
        order.setDeliveryId(deliveryDto.getDeliveryId());
        return OrderDtoMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto failAssemblyOrder(UUID orderId) {
        Order order = findById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return OrderDtoMapper.mapToOrderDto(order);
    }

    private Order findById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Заказ с id: " + orderId + " не найден"));
    }

    private Address findExistAddressOrGetNew(AddressDto addressDto) {
        Optional<Address> addressOpt = addressRepository.findByCountryAndCityAndStreetAndHouseAndFlat(addressDto.getCountry(),
                addressDto.getCity(), addressDto.getStreet(), addressDto.getHouse(), addressDto.getFlat());
        return addressOpt.orElseGet(() -> addressRepository.save(AddressDtoMapper.mapToNewAddress(addressDto)));
    }

    private DeliveryParametersDto bookProducts(Order order) {
        return warehouseClient.bookProducts(BookProductsRequest.builder()
                .orderId(order.getId())
                .products(order.getProducts())
                .build());
    }
}
