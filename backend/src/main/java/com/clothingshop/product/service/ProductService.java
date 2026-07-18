package com.clothingshop.product.service;

import com.clothingshop.common.exception.ResourceNotFoundException;
import com.clothingshop.product.dto.CategoryResponse;
import com.clothingshop.product.dto.ProductResponse;
import com.clothingshop.product.dto.VariantResponse;
import com.clothingshop.product.entity.Category;
import com.clothingshop.product.entity.Product;
import com.clothingshop.product.entity.ProductVariant;
import com.clothingshop.product.repository.CategoryRepository;
import com.clothingshop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(int page, int size, Long categoryId, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products;

        if (categoryId != null && search != null && !search.isBlank()) {
            products = productRepository.findByCategoryIdAndSearch(categoryId, search, pageable);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
        } else if (search != null && !search.isBlank()) {
            products = productRepository.searchByName(search, pageable);
        } else {
            products = productRepository.findByIsActiveTrue(pageable);
        }

        return products.map(this::toProductResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
        return toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll(Sort.by("name").ascending());
        return categories.stream().map(this::toCategoryResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, int page, int size) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", categoryId);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
        return products.map(this::toProductResponse);
    }

    private ProductResponse toProductResponse(Product product) {
        List<VariantResponse> variantResponses = product.getVariants().stream()
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
                .variants(variantResponses)
                .active(product.isActive())
                .build();
    }

    private VariantResponse toVariantResponse(ProductVariant variant) {
        BigDecimal totalPrice = variant.getProduct().getBasePrice().add(variant.getPriceAdjustment());

        return VariantResponse.builder()
                .id(variant.getId())
                .size(variant.getSize())
                .color(variant.getColor())
                .colorHex(variant.getColorHex())
                .priceAdjustment(variant.getPriceAdjustment())
                .totalPrice(totalPrice)
                .stockQuantity(variant.getStockQuantity())
                .sku(variant.getSku())
                .imageUrl(variant.getImageUrl())
                .active(variant.isActive())
                .build();
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }
}
