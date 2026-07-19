package com.clothingshop.admin.service;

import com.clothingshop.admin.dto.*;
import com.clothingshop.common.exception.ResourceNotFoundException;
import com.clothingshop.order.entity.Order;
import com.clothingshop.order.repository.OrderRepository;
import com.clothingshop.product.dto.ProductResponse;
import com.clothingshop.product.dto.VariantResponse;
import com.clothingshop.product.entity.Category;
import com.clothingshop.product.entity.Product;
import com.clothingshop.product.entity.ProductVariant;
import com.clothingshop.product.repository.CategoryRepository;
import com.clothingshop.product.repository.ProductRepository;
import com.clothingshop.product.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .imageUrl(request.getImageUrl())
                .category(category)
                .isActive(request.isActive())
                .build();

        product = productRepository.save(product);
        return toProductResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getSlug() != null) product.setSlug(request.getSlug());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getBasePrice() != null) product.setBasePrice(request.getBasePrice());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
            product.setCategory(category);
        }
        if (request.getActive() != null) product.setActive(request.getActive());

        product = productRepository.save(product);
        return toProductResponse(product);
    }

    @Transactional
    public VariantResponse createVariant(Long productId, CreateVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .size(request.getSize())
                .color(request.getColor())
                .colorHex(request.getColorHex())
                .priceAdjustment(request.getPriceAdjustment())
                .stockQuantity(request.getStockQuantity())
                .sku(request.getSku())
                .imageUrl(request.getImageUrl())
                .isActive(request.isActive())
                .build();

        variant = productVariantRepository.save(variant);
        return toVariantResponse(variant);
    }

    @Transactional
    public VariantResponse updateVariant(Long variantId, UpdateVariantRequest request) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", variantId));

        if (request.getStockQuantity() != null) {
            if (request.getStockQuantity() < 0) {
                throw new IllegalArgumentException("Stock quantity cannot be negative");
            }
            variant.setStockQuantity(request.getStockQuantity());
        }
        if (request.getPriceAdjustment() != null) variant.setPriceAdjustment(request.getPriceAdjustment());
        if (request.getActive() != null) variant.setActive(request.getActive());

        variant = productVariantRepository.save(variant);
        return toVariantResponse(variant);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        Order.OrderStatus newStatus;
        try {
            newStatus = Order.OrderStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + request.getStatus());
        }

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update a cancelled order");
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    private ProductResponse toProductResponse(Product product) {
        List<VariantResponse> variants = product.getVariants().stream()
                .filter(ProductVariant::isActive)
                .map(this::toVariantResponse)
                .toList();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .variants(variants)
                .active(product.isActive())
                .build();
    }

    private VariantResponse toVariantResponse(ProductVariant variant) {
        return VariantResponse.builder()
                .id(variant.getId())
                .size(variant.getSize())
                .color(variant.getColor())
                .colorHex(variant.getColorHex())
                .priceAdjustment(variant.getPriceAdjustment())
                .totalPrice(variant.getProduct().getBasePrice().add(variant.getPriceAdjustment()))
                .stockQuantity(variant.getStockQuantity())
                .sku(variant.getSku())
                .imageUrl(variant.getImageUrl())
                .active(variant.isActive())
                .build();
    }
}
