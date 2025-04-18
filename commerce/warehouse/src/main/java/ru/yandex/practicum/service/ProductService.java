package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.NewProductWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ProductDtoWarehouse;

public interface ProductService {

    ProductDtoWarehouse addNewProduct(NewProductWarehouseRequest productDto);

    ProductDtoWarehouse addQuantityProducts(AddProductToWarehouseRequest request);
}
