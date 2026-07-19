package com.clothingshop.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateProductRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String slug;

    private String description;

    @NotNull
    @Positive
    private BigDecimal basePrice;

    private String imageUrl;

    @NotNull
    private Long categoryId;

    private boolean active = true;
}
