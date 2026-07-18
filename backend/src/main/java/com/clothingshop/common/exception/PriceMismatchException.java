package com.clothingshop.common.exception;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class PriceMismatchException extends RuntimeException {
    private final Long variantId;
    private final String variantInfo;
    private final BigDecimal expectedPrice;
    private final BigDecimal actualPrice;

    public PriceMismatchException(Long variantId, String variantInfo, BigDecimal expectedPrice, BigDecimal actualPrice) {
        super(String.format("Price changed for %s: expected %s, current %s", variantInfo, expectedPrice, actualPrice));
        this.variantId = variantId;
        this.variantInfo = variantInfo;
        this.expectedPrice = expectedPrice;
        this.actualPrice = actualPrice;
    }
}
