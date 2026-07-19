package com.clothingshop.admin.controller;

import com.clothingshop.admin.dto.CreateProductRequest;
import com.clothingshop.admin.dto.CreateVariantRequest;
import com.clothingshop.admin.dto.UpdateProductRequest;
import com.clothingshop.admin.dto.UpdateVariantRequest;
import com.clothingshop.admin.service.AdminService;
import com.clothingshop.product.dto.ProductResponse;
import com.clothingshop.product.dto.VariantResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(adminService.updateProduct(id, request));
    }

    @PostMapping("/{productId}/variants")
    public ResponseEntity<VariantResponse> createVariant(@PathVariable Long productId,
                                                          @Valid @RequestBody CreateVariantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createVariant(productId, request));
    }

    @PutMapping("/variants/{variantId}")
    public ResponseEntity<VariantResponse> updateVariant(@PathVariable Long variantId,
                                                          @Valid @RequestBody UpdateVariantRequest request) {
        return ResponseEntity.ok(adminService.updateVariant(variantId, request));
    }
}
