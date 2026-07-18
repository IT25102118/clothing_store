package com.clothingshop.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantResponse {
    private Long id;
    private String size;
    private String color;
    private String colorHex;
    private BigDecimal priceAdjustment;
    private BigDecimal totalPrice;
    private int stockQuantity;
    private String sku;
    private String imageUrl;
    private boolean active;
}
