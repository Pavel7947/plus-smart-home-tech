package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.NewProductWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ProductDtoWarehouse;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ProductDtoMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public ProductDtoWarehouse addNewProduct(NewProductWarehouseRequest productDto) {
        if (productRepository.existsById(productDto.getProductId())) {
            throw new DuplicateException("Продукт с id: " + productDto.getProductId() + " уже существует");
        }
        return ProductDtoMapper.mapToProductDtoResponse(productRepository.save(ProductDtoMapper.mapToProduct(productDto)));
    }

    @Override
    public ProductDtoWarehouse addQuantityProducts(AddProductToWarehouseRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Товар c id: " + request.getProductId() + " на складе не найден"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        return ProductDtoMapper.mapToProductDtoResponse(product);
    }
}