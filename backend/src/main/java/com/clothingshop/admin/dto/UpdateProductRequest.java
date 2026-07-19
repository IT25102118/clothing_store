package com.clothingshop.admin.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    private String name;
    private String slug;
    private String description;

    @Positive
    private BigDecimal basePrice;

    private String imageUrl;
    private Long categoryId;
    private Boolean active;
}
