package com.clothingshop.product.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllCategories_ShouldReturnList() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/categories", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Tops");
    }

    @Test
    void getProductsByCategory_ShouldReturnPaginatedProducts() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/categories/1/products?page=0&size=10", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("content");
    }
}
