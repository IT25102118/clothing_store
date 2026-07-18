package com.clothingshop.order.service;

import com.clothingshop.cart.entity.Cart;
import com.clothingshop.cart.entity.CartItem;
import com.clothingshop.cart.repository.CartRepository;
import com.clothingshop.common.exception.InsufficientStockException;
import com.clothingshop.common.exception.PriceMismatchException;
import com.clothingshop.common.exception.ResourceNotFoundException;
import com.clothingshop.order.dto.CheckoutRequest;
import com.clothingshop.order.dto.OrderItemResponse;
import com.clothingshop.order.dto.OrderResponse;
import com.clothingshop.order.entity.Order;
import com.clothingshop.order.entity.OrderItem;
import com.clothingshop.order.repository.OrderRepository;
import com.clothingshop.product.entity.ProductVariant;
import com.clothingshop.product.repository.ProductVariantRepository;
import com.clothingshop.user.entity.User;
import com.clothingshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse checkout(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("No active cart found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = productVariantRepository
                    .findByIdWithPessimisticLock(cartItem.getVariant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", cartItem.getVariant().getId()));

            if (!variant.isActive()) {
                throw new IllegalArgumentException("Variant '" + variant.getProduct().getName()
                        + " (" + variant.getSize() + ", " + variant.getColor() + ")' is no longer available");
            }

            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        variant.getId(),
                        variant.getProduct().getName() + " (" + variant.getSize() + ", " + variant.getColor() + ")",
                        cartItem.getQuantity(),
                        variant.getStockQuantity()
                );
            }

            BigDecimal currentPrice = variant.getProduct().getBasePrice().add(variant.getPriceAdjustment());
            if (currentPrice.compareTo(cartItem.getUnitPrice()) != 0) {
                throw new PriceMismatchException(
                        variant.getId(),
                        variant.getProduct().getName() + " (" + variant.getSize() + ", " + variant.getColor() + ")",
                        cartItem.getUnitPrice(),
                        currentPrice
                );
            }

            variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
            productVariantRepository.save(variant);

            BigDecimal subtotal = currentPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .variant(variant)
                    .productName(variant.getProduct().getName())
                    .variantSize(variant.getSize())
                    .variantColor(variant.getColor())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(currentPrice)
                    .subtotal(subtotal)
                    .build();
            orderItems.add(orderItem);
        }

        Order order = Order.builder()
                .user(user)
                .status(Order.OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .shippingAddress(request.getShippingAddress())
                .paymentStatus(Order.PaymentStatus.UNPAID)
                .items(orderItems)
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        order = orderRepository.save(order);

        cart.setStatus(Cart.CartStatus.CONVERTED);
        cartRepository.save(cart);

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to user");
        }
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .variantId(item.getVariant().getId())
                        .productName(item.getProductName())
                        .productSlug(item.getVariant().getProduct().getSlug())
                        .productImageUrl(item.getVariant().getProduct().getImageUrl())
                        .size(item.getVariantSize())
                        .color(item.getVariantColor())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .paymentStatus(order.getPaymentStatus().name())
                .items(items)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
