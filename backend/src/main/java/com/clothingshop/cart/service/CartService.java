package com.clothingshop.cart.service;

import com.clothingshop.cart.dto.AddCartItemRequest;
import com.clothingshop.cart.dto.CartItemResponse;
import com.clothingshop.cart.dto.CartResponse;
import com.clothingshop.cart.dto.UpdateCartItemRequest;
import com.clothingshop.cart.entity.Cart;
import com.clothingshop.cart.entity.CartItem;
import com.clothingshop.cart.repository.CartItemRepository;
import com.clothingshop.cart.repository.CartRepository;
import com.clothingshop.common.exception.InsufficientStockException;
import com.clothingshop.common.exception.ResourceNotFoundException;
import com.clothingshop.product.entity.ProductVariant;
import com.clothingshop.product.repository.ProductVariantRepository;
import com.clothingshop.user.entity.User;
import com.clothingshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CartResponse getActiveCart(Long userId) {
        Cart cart = getOrCreateActiveCart(userId);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(Long userId, AddCartItemRequest request) {
        Cart cart = getOrCreateActiveCart(userId);

        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", request.getVariantId()));

        if (!variant.isActive()) {
            throw new IllegalArgumentException("Variant is no longer available");
        }

        if (variant.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    variant.getId(),
                    variant.getProduct().getName() + " (" + variant.getSize() + ", " + variant.getColor() + ")",
                    request.getQuantity(),
                    variant.getStockQuantity()
            );
        }

        cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(variant.getId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> {
                            int newQty = existing.getQuantity() + request.getQuantity();
                            if (newQty > variant.getStockQuantity()) {
                                throw new InsufficientStockException(
                                        variant.getId(),
                                        variant.getProduct().getName() + " (" + variant.getSize() + ", " + variant.getColor() + ")",
                                        newQty,
                                        variant.getStockQuantity()
                                );
                            }
                            existing.setQuantity(newQty);
                            cartItemRepository.save(existing);
                        },
                        () -> {
                            BigDecimal unitPrice = variant.getProduct().getBasePrice().add(variant.getPriceAdjustment());
                            CartItem item = CartItem.builder()
                                    .cart(cart)
                                    .variant(variant)
                                    .quantity(request.getQuantity())
                                    .unitPrice(unitPrice)
                                    .build();
                            cart.getItems().add(item);
                            cartItemRepository.save(item);
                        }
                );

        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateActiveCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Item does not belong to user's active cart");
        }

        if (request.getQuantity() > item.getVariant().getStockQuantity()) {
            throw new InsufficientStockException(
                    item.getVariant().getId(),
                    item.getVariant().getProduct().getName() + " (" + item.getVariant().getSize() + ", " + item.getVariant().getColor() + ")",
                    request.getQuantity(),
                    item.getVariant().getStockQuantity()
            );
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateActiveCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Item does not belong to user's active cart");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return toResponse(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateActiveCart(userId);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
    }

    private Cart getOrCreateActiveCart(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
                    Cart newCart = Cart.builder()
                            .user(user)
                            .status(Cart.CartStatus.ACTIVE)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalItems(totalItems)
                .subtotal(subtotal)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemResponse.builder()
                .id(item.getId())
                .variantId(item.getVariant().getId())
                .productName(item.getVariant().getProduct().getName())
                .productSlug(item.getVariant().getProduct().getSlug())
                .productImageUrl(item.getVariant().getProduct().getImageUrl())
                .size(item.getVariant().getSize())
                .color(item.getVariant().getColor())
                .colorHex(item.getVariant().getColorHex())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}
