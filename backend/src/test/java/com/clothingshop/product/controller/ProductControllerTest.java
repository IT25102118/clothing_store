package com.clothingshop.product.controller;

import com.clothingshop.common.exception.GlobalExceptionHandler;
import com.clothingshop.common.exception.ResourceNotFoundException;
import com.clothingshop.product.dto.ProductResponse;
import com.clothingshop.product.dto.VariantResponse;
import com.clothingshop.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        VariantResponse variant = VariantResponse.builder()
                .id(1L).size("M").color("Black").colorHex("#000000")
                .priceAdjustment(BigDecimal.ZERO).totalPrice(BigDecimal.valueOf(29.99))
                .stockQuantity(10).sku("TEE-BLACK-M").active(true).build();

        productResponse = ProductResponse.builder()
                .id(1L).name("Classic Tee").slug("classic-tee").description("A classic tee")
                .basePrice(BigDecimal.valueOf(29.99)).imageUrl("/images/tee.jpg")
                .categoryId(1L).categoryName("Tops").active(true)
                .variants(List.of(variant)).build();
    }

    @Test
    void getProducts_ShouldReturnPaginatedResults() throws Exception {
        Page<ProductResponse> page = new PageImpl<>(List.of(productResponse));
        when(productService.getProducts(anyInt(), anyInt(), isNull(), isNull())).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Classic Tee"))
                .andExpect(jsonPath("$.content[0].variants[0].size").value("M"));
    }

    @Test
    void getProducts_WithCategoryFilter_ShouldFilter() throws Exception {
        Page<ProductResponse> page = new PageImpl<>(List.of(productResponse));
        when(productService.getProducts(anyInt(), anyInt(), eq(1L), isNull())).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("categoryId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productResponse);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Classic Tee"))
                .andExpect(jsonPath("$.categoryName").value("Tops"));
    }

    @Test
    void getProductById_ShouldReturn404WhenNotFound() throws Exception {
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product", 99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));
    }

    @Test
    void getProductBySlug_ShouldReturnProduct() throws Exception {
        when(productService.getProductBySlug("classic-tee")).thenReturn(productResponse);

        mockMvc.perform(get("/api/products/slug/classic-tee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("classic-tee"));
    }

    @Test
    void getProductBySlug_ShouldReturn404WhenNotFound() throws Exception {
        when(productService.getProductBySlug("unknown"))
                .thenThrow(new ResourceNotFoundException("Product", "slug", "unknown"));

        mockMvc.perform(get("/api/products/slug/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with slug: unknown"));
    }
}
