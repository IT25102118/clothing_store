package com.clothingshop.product.controller;

import com.clothingshop.product.dto.ProductResponse;
import com.clothingshop.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog endpoints")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List active products", description = "Returns a paginated list of active products with optional category and search filters")
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (zero-based") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size") int size,
            @RequestParam(required = false) @Parameter(description = "Filter by category ID") Long categoryId,
            @RequestParam(required = false) @Parameter(description = "Search by product name") String search) {
        return ResponseEntity.ok(productService.getProducts(page, size, categoryId, search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Returns a single active product with its variants")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product by slug", description = "Returns a single active product identified by its URL slug")
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getProductBySlug(slug));
    }
}
