package com.clothingshop.admin.service;

import com.clothingshop.admin.dto.*;
import com.clothingshop.common.exception.ResourceNotFoundException;
import com.clothingshop.product.dto.CategoryResponse;
import com.clothingshop.product.dto.ProductResponse;
import com.clothingshop.product.dto.VariantResponse;
import com.clothingshop.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProductService productService;

    private Long categoryId;

    @BeforeEach
    void setUp() {
        List<CategoryResponse> categories = productService.getAllCategories();
        categoryId = categories.get(0).getId();
    }

    @Test
    void createProduct_CreatesProduct() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Admin Test Product");
        request.setSlug("admin-test-product-" + System.currentTimeMillis());
        request.setBasePrice(BigDecimal.valueOf(39.99));
        request.setCategoryId(categoryId);

        ProductResponse response = adminService.createProduct(request);
        assertThat(response.getName()).isEqualTo("Admin Test Product");
        assertThat(response.getCategoryId()).isEqualTo(categoryId);
    }

    @Test
    void createProduct_ThrowsForInvalidCategory() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Bad Category Product");
        request.setSlug("bad-category-" + System.currentTimeMillis());
        request.setBasePrice(BigDecimal.TEN);
        request.setCategoryId(99999L);

        assertThrows(ResourceNotFoundException.class, () ->
                adminService.createProduct(request));
    }

    @Test
    void updateProduct_UpdatesFields() {
        CreateProductRequest createReq = new CreateProductRequest();
        createReq.setName("Update Test");
        createReq.setSlug("update-test-" + System.currentTimeMillis());
        createReq.setBasePrice(BigDecimal.valueOf(19.99));
        createReq.setCategoryId(categoryId);
        ProductResponse created = adminService.createProduct(createReq);

        UpdateProductRequest updateReq = new UpdateProductRequest();
        updateReq.setName("Updated Name");
        updateReq.setBasePrice(BigDecimal.valueOf(24.99));

        ProductResponse updated = adminService.updateProduct(created.getId(), updateReq);
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(24.99));
    }

    @Test
    void createVariant_CreatesVariant() {
        CreateProductRequest createReq = new CreateProductRequest();
        createReq.setName("Variant Test");
        createReq.setSlug("variant-test-" + System.currentTimeMillis());
        createReq.setBasePrice(BigDecimal.valueOf(59.99));
        createReq.setCategoryId(categoryId);
        ProductResponse product = adminService.createProduct(createReq);

        CreateVariantRequest variantReq = new CreateVariantRequest();
        variantReq.setSize("XL");
        variantReq.setColor("Green");
        variantReq.setColorHex("#00FF00");
        variantReq.setStockQuantity(15);
        variantReq.setSku("VT-" + System.currentTimeMillis());

        VariantResponse variant = adminService.createVariant(product.getId(), variantReq);
        assertThat(variant.getSize()).isEqualTo("XL");
        assertThat(variant.getStockQuantity()).isEqualTo(15);
    }

    @Test
    void updateVariant_UpdatesStock() {
        CreateProductRequest createReq = new CreateProductRequest();
        createReq.setName("Stock Test");
        createReq.setSlug("stock-test-" + System.currentTimeMillis());
        createReq.setBasePrice(BigDecimal.valueOf(69.99));
        createReq.setCategoryId(categoryId);
        ProductResponse product = adminService.createProduct(createReq);

        CreateVariantRequest variantReq = new CreateVariantRequest();
        variantReq.setSize("S");
        variantReq.setColor("Yellow");
        variantReq.setStockQuantity(10);
        variantReq.setSku("ST-" + System.currentTimeMillis());
        VariantResponse variant = adminService.createVariant(product.getId(), variantReq);

        UpdateVariantRequest updateReq = new UpdateVariantRequest();
        updateReq.setStockQuantity(25);
        VariantResponse updated = adminService.updateVariant(variant.getId(), updateReq);
        assertThat(updated.getStockQuantity()).isEqualTo(25);
    }

    @Test
    void updateVariant_ThrowsForNegativeStock() {
        CreateProductRequest createReq = new CreateProductRequest();
        createReq.setName("Neg Stock Test");
        createReq.setSlug("neg-stock-" + System.currentTimeMillis());
        createReq.setBasePrice(BigDecimal.valueOf(79.99));
        createReq.setCategoryId(categoryId);
        ProductResponse product = adminService.createProduct(createReq);

        CreateVariantRequest variantReq = new CreateVariantRequest();
        variantReq.setSize("M");
        variantReq.setColor("White");
        variantReq.setStockQuantity(5);
        variantReq.setSku("NEG-" + System.currentTimeMillis());
        VariantResponse variant = adminService.createVariant(product.getId(), variantReq);

        UpdateVariantRequest updateReq = new UpdateVariantRequest();
        updateReq.setStockQuantity(-1);

        assertThrows(IllegalArgumentException.class, () ->
                adminService.updateVariant(variant.getId(), updateReq));
    }
}
