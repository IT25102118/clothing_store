package com.clothingshop.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long variantId;
    private String productName;
    private String productSlug;
    private String productImageUrl;
    private String size;
    private String color;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
