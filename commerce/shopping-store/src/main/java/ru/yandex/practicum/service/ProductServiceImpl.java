package ru.yandex.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.GetProductsFilter;
import ru.yandex.practicum.dto.shoppingstore.ProductCategory;
import ru.yandex.practicum.dto.shoppingstore.ProductDtoStore;
import ru.yandex.practicum.dto.shoppingstore.ProductState;
import ru.yandex.practicum.dto.shoppingstore.QuantityState;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ProductDtoMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.QProduct;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;


    @Override
    public List<ProductDtoStore> getProducts(GetProductsFilter filter) {
        BooleanExpression condition = Expressions.TRUE;
        Set<UUID> productsIds = filter.getProductsIds();
        if (productsIds != null && !productsIds.isEmpty()) {
            condition = condition.and(QProduct.product.id.in(productsIds));
        }
        if (filter.getCategory() != null) {
            condition = condition.and(QProduct.product.productCategory.eq(filter.getCategory()));
        }
        List<Product> products = productRepository.findAll(condition, filter.getPageable()).getContent();
        if (productsIds != null && !productsIds.isEmpty()) {
            checkAllProductsFound(products, productsIds);
        }
        return ProductDtoMapper.mapToProductDto(products);
    }

    @Override
    @Transactional
    public ProductDtoStore createProduct(ProductDtoStore newProduct) {
        return ProductDtoMapper.mapToProductDto(productRepository.save(ProductDtoMapper.mapToProduct(newProduct)));
    }

    @Override
    @Transactional
    public ProductDtoStore updateProduct(ProductDtoStore updatedProduct) {
        Product product = getProductEntityById(updatedProduct.getId());
        String name = updatedProduct.getProductName();
        if (name != null && !name.isBlank()) product.setProductName(name);
        String description = updatedProduct.getDescription();
        if (description != null && !description.isBlank()) product.setDescription(description);
        String imageSrc = updatedProduct.getImageSrc();
        if (imageSrc != null && !imageSrc.isBlank()) product.setImageSrc(imageSrc);
        ProductState productState = updatedProduct.getProductState();
        if (productState != null) product.setProductState(productState);
        QuantityState quantityState = updatedProduct.getQuantityState();
        if (quantityState != null) product.setQuantityState(quantityState);
        ProductCategory productCategory = updatedProduct.getProductCategory();
        if (productCategory != null) product.setProductCategory(productCategory);
        Double price = updatedProduct.getPrice();
        if (price != null) product.setPrice(price);
        return ProductDtoMapper.mapToProductDto(product);
    }

    @Override
    @Transactional
    public ProductDtoStore removeProduct(UUID productId) {
        Product product = getProductEntityById(productId);
        productRepository.delete(product);
        return ProductDtoMapper.mapToProductDto(product);
    }

    @Override
    @Transactional
    public ProductDtoStore setQuantityState(UUID productId, QuantityState quantityState) {
        Product product = getProductEntityById(productId);
        product.setQuantityState(quantityState);
        return ProductDtoMapper.mapToProductDto(product);
    }

    @Override
    public ProductDtoStore getProductById(UUID productId) {
        return ProductDtoMapper.mapToProductDto(getProductEntityById(productId));
    }

    private Product getProductEntityById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("По переданному id: " + productId + " товар не найден"));
    }

    private void checkAllProductsFound(List<Product> products, Set<UUID> requiredProductIds) {
        if (products.size() < requiredProductIds.size()) {
            Set<UUID> findProductIds = products.stream().map(Product::getId).collect(Collectors.toSet());
            String missingProductIds = requiredProductIds.stream().filter(uuid -> !findProductIds.contains(uuid))
                    .map(UUID::toString).collect(Collectors.joining(", "));
            throw new NotFoundException("Некоторые товары  не найдены id: " + missingProductIds);
        }
    }

}
