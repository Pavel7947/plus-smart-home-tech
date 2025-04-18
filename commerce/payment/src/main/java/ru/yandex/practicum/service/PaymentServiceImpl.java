package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.shoppingstore.ProductDtoStore;
import ru.yandex.practicum.exception.BadRequestException;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.PaymentDtoMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.PaymentState;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final ShoppingStoreClient storeClient;

    @Override
    @Transactional
    public PaymentDto cancelPayment(OrderDto orderDto) {
        Payment payment = findById(orderDto.getPaymentId());
        payment.setState(PaymentState.CANCELED);
        return PaymentDtoMapper.mapToPaymentDto(payment);
    }

    @Override
    @Transactional
    public PaymentDto payOrder(OrderDto orderDto) {
        UUID orderId = orderDto.getId();
        if (paymentRepository.existsByOrderId(orderId)) {
            throw new DuplicateException("Запрос на оплату для заказа с id: " + orderId + " уже существует");
        }
        if (orderDto.getTotalPrice() == null) {
            throw new BadRequestException("Не произведен расчет общей стоимости заказа");
        }
        Payment payment = paymentRepository.save(Payment.builder()
                .orderId(orderId)
                .deliveryCost(orderDto.getDeliveryPrice())
                .productCost(orderDto.getProductPrice())
                .totalCost(orderDto.getTotalPrice())
                .build());
        return PaymentDtoMapper.mapToPaymentDto(payment);
    }

    @Override
    public OrderDto calculateTotalCost(OrderDto orderDto) {
        if (orderDto.getDeliveryPrice() == null) {
            throw new BadRequestException("Нужно сначала расчитать стоимость доставки!");
        }
        double productCost = orderDto.getProductPrice() + orderDto.getProductPrice() * 0.1;
        double totalCost = productCost + orderDto.getDeliveryPrice();
        totalCost = Math.ceil(totalCost * 100) / 100;
        orderDto.setTotalPrice(totalCost);
        return orderDto;
    }

    @Override
    @Transactional
    public PaymentDto confirmPayment(UUID paymentId) {
        Payment payment = findById(paymentId);
        payment.setState(PaymentState.SUCCESS);
        orderClient.confirmPaymentOrder(payment.getOrderId());
        return PaymentDtoMapper.mapToPaymentDto(payment);
    }

    @Override
    public OrderDto calculateProductCost(OrderDto orderDto) {
        Map<UUID, Long> quantityMap = orderDto.getProducts();
        List<ProductDtoStore> products = storeClient.getProducts(null, quantityMap.keySet(), 0,
                quantityMap.size(), null);
        double totalCost = products.stream().map(product -> product.getPrice() * quantityMap.get(product.getId())).
                reduce((double) 0, Double::sum);
        totalCost = Math.ceil(totalCost * 100) / 100.0;
        orderDto.setProductPrice(totalCost);
        return orderDto;
    }

    @Override
    @Transactional
    public PaymentDto failPayment(UUID paymentId) {
        Payment payment = findById(paymentId);
        payment.setState(PaymentState.FAILED);
        orderClient.failPaymentOrder(payment.getOrderId());
        return PaymentDtoMapper.mapToPaymentDto(payment);
    }

    private Payment findById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Оплата с id: " + paymentId + " не найдена"));
    }
}
