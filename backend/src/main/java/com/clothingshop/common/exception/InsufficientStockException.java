package com.clothingshop.common.exception;

import lombok.Getter;

@Getter
public class InsufficientStockException extends RuntimeException {
    private final Long variantId;
    private final String variantInfo;
    private final int requested;
    private final int available;

    public InsufficientStockException(Long variantId, String variantInfo, int requested, int available) {
        super(String.format("Insufficient stock for %s: requested %d, available %d", variantInfo, requested, available));
        this.variantId = variantId;
        this.variantInfo = variantInfo;
        this.requested = requested;
        this.available = available;
    }
}
