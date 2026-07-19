package com.clothingshop.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateVariantRequest {
    @NotBlank
    private String size;

    @NotBlank
    private String color;

    private String colorHex;

    private BigDecimal priceAdjustment = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private int stockQuantity;

    @NotBlank
    private String sku;

    private String imageUrl;

    private boolean active = true;
}
