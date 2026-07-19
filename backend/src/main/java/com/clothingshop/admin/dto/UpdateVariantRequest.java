package com.clothingshop.admin.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateVariantRequest {
    @PositiveOrZero
    private Integer stockQuantity;

    private BigDecimal priceAdjustment;

    private Boolean active;
}
