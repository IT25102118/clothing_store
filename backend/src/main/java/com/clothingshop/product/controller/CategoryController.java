package com.clothingshop.product.controller;

import com.clothingshop.product.dto.CategoryResponse;
import com.clothingshop.product.dto.ProductResponse;
import com.clothingshop.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product category endpoints")
public class CategoryController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List all categories", description = "Returns all available product categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @GetMapping("/{id}/products")
    @Operation(summary = "List products by category", description = "Returns a paginated list of active products in a specific category")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable @Parameter(description = "Category ID") Long id,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (zero-based") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size") int size) {
        return ResponseEntity.ok(productService.getProductsByCategory(id, page, size));
    }
}
