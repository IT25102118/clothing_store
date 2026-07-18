package com.clothingshop.product.controller;

import com.clothingshop.common.exception.GlobalExceptionHandler;
import com.clothingshop.common.exception.ResourceNotFoundException;
import com.clothingshop.product.dto.CategoryResponse;
import com.clothingshop.product.dto.ProductResponse;
import com.clothingshop.product.service.ProductService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getAllCategories_ShouldReturnList() throws Exception {
        CategoryResponse category = CategoryResponse.builder()
                .id(1L).name("Tops").slug("tops").description("Tops category").build();
        when(productService.getAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tops"));
    }

    @Test
    void getProductsByCategory_ShouldReturnPaginatedProducts() throws Exception {
        Page<ProductResponse> emptyPage = Page.empty();
        when(productService.getProductsByCategory(anyLong(), anyInt(), anyInt())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/categories/1/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getProductsByCategory_ShouldReturn404WhenCategoryNotFound() throws Exception {
        when(productService.getProductsByCategory(99L, 0, 10))
                .thenThrow(new ResourceNotFoundException("Category", 99L));

        mockMvc.perform(get("/api/categories/99/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found with id: 99"));
    }
}
