package com.clothingshop.product.service;

import com.clothingshop.common.exception.ResourceNotFoundException;
import com.clothingshop.product.dto.ProductResponse;
import com.clothingshop.product.entity.Category;
import com.clothingshop.product.entity.Product;
import com.clothingshop.product.entity.ProductVariant;
import com.clothingshop.product.repository.CategoryRepository;
import com.clothingshop.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, categoryRepository);
    }

    @Test
    void getProducts_ShouldReturnAllActiveProducts() {
        Category category = createCategory(1L, "Tops");
        Product product = createProduct(1L, "Classic Tee", "classic-tee", category);
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        when(productRepository.findByIsActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponse> result = productService.getProducts(0, 10, null, null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Classic Tee");
    }

    @Test
    void getProducts_ShouldFilterByCategory() {
        Category category = createCategory(1L, "Tops");
        Product product = createProduct(1L, "Classic Tee", "classic-tee", category);
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        when(productRepository.findByCategoryIdAndIsActiveTrue(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponse> result = productService.getProducts(0, 10, 1L, null);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getProducts_ShouldSearchByName() {
        Category category = createCategory(1L, "Tops");
        Product product = createProduct(1L, "Classic Tee", "classic-tee", category);
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        when(productRepository.searchByName("tee", pageable))
                .thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponse> result = productService.getProducts(0, 10, null, "tee");

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        Category category = createCategory(1L, "Tops");
        Product product = createProduct(1L, "Classic Tee", "classic-tee", category);
        when(productRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(product));

        var result = productService.getProductById(1L);

        assertThat(result.getName()).isEqualTo("Classic Tee");
        assertThat(result.getSlug()).isEqualTo("classic-tee");
    }

    @Test
    void getProductById_ShouldThrowWhenNotFound() {
        when(productRepository.findByIdAndIsActiveTrue(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 99");
    }

    @Test
    void getProductBySlug_ShouldReturnProduct() {
        Category category = createCategory(1L, "Tops");
        Product product = createProduct(1L, "Classic Tee", "classic-tee", category);
        when(productRepository.findBySlugAndIsActiveTrue("classic-tee")).thenReturn(Optional.of(product));

        var result = productService.getProductBySlug("classic-tee");

        assertThat(result.getSlug()).isEqualTo("classic-tee");
    }

    @Test
    void getProductBySlug_ShouldThrowWhenNotFound() {
        when(productRepository.findBySlugAndIsActiveTrue("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductBySlug("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with slug: unknown");
    }

    @Test
    void getAllCategories_ShouldReturnAll() {
        Category cat = createCategory(1L, "Tops");
        when(categoryRepository.findAll(Sort.by("name").ascending())).thenReturn(List.of(cat));

        var result = productService.getAllCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Tops");
    }

    @Test
    void getProductsByCategory_ShouldThrowWhenCategoryNotFound() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.getProductsByCategory(99L, 0, 10))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: 99");
    }

    private Category createCategory(Long id, String name) {
        Category cat = new Category();
        cat.setId(id);
        cat.setName(name);
        cat.setSlug(name.toLowerCase());
        return cat;
    }

    private Product createProduct(Long id, String name, String slug, Category category) {
        ProductVariant variant = new ProductVariant();
        variant.setId(1L);
        variant.setSize("M");
        variant.setColor("Black");
        variant.setPriceAdjustment(BigDecimal.ZERO);
        variant.setStockQuantity(10);
        variant.setSku("TEE-BLACK-M");
        variant.setActive(true);

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setSlug(slug);
        product.setDescription("Description");
        product.setBasePrice(BigDecimal.valueOf(29.99));
        product.setCategory(category);
        product.setActive(true);
        product.setVariants(List.of(variant));
        variant.setProduct(product);
        return product;
    }
}
