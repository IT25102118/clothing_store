package com.clothingshop.product.service;

import com.clothingshop.product.dto.CategoryResponse;
import com.clothingshop.product.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void getProducts_ReturnsPaginatedResults() {
        Page<ProductResponse> page = productService.getProducts(0, 5, null, null);
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getSize()).isEqualTo(5);
    }

    @Test
    void getProducts_WithCategoryFilter() {
        List<CategoryResponse> categories = productService.getAllCategories();
        assertThat(categories).isNotEmpty();

        Long categoryId = categories.get(0).getId();
        Page<ProductResponse> page = productService.getProducts(0, 10, categoryId, null);
        assertThat(page.getContent()).allMatch(p -> p.getCategoryId().equals(categoryId));
    }

    @Test
    void getProducts_WithSearch() {
        Page<ProductResponse> page = productService.getProducts(0, 10, null, "shirt");
        assertThat(page).isNotNull();
    }

    @Test
    void getProductById_ReturnsProduct() {
        Page<ProductResponse> all = productService.getProducts(0, 1, null, null);
        if (!all.getContent().isEmpty()) {
            Long id = all.getContent().get(0).getId();
            ProductResponse product = productService.getProductById(id);
            assertThat(product.getId()).isEqualTo(id);
        }
    }

    @Test
    void getProductBySlug_ReturnsProduct() {
        Page<ProductResponse> all = productService.getProducts(0, 1, null, null);
        if (!all.getContent().isEmpty()) {
            String slug = all.getContent().get(0).getSlug();
            ProductResponse product = productService.getProductBySlug(slug);
            assertThat(product.getSlug()).isEqualTo(slug);
        }
    }

    @Test
    void getAllCategories_ReturnsCategories() {
        List<CategoryResponse> categories = productService.getAllCategories();
        assertThat(categories).isNotEmpty();
        assertThat(categories).allMatch(c -> c.getName() != null);
    }
}
