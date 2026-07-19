package com.clothingshop.cart.service;

import com.clothingshop.cart.dto.AddCartItemRequest;
import com.clothingshop.cart.dto.CartResponse;
import com.clothingshop.cart.dto.UpdateCartItemRequest;
import com.clothingshop.common.exception.InsufficientStockException;
import com.clothingshop.common.exception.ResourceNotFoundException;
import com.clothingshop.product.entity.Product;
import com.clothingshop.product.entity.ProductVariant;
import com.clothingshop.product.repository.ProductRepository;
import com.clothingshop.product.repository.ProductVariantRepository;
import com.clothingshop.user.entity.User;
import com.clothingshop.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private ProductVariant variant;

    @BeforeEach
    void setUp() {
        String uid = String.valueOf(System.currentTimeMillis());
        testUser = userRepository.save(User.builder()
                .email("cart-test-" + uid + "@test.com")
                .password(passwordEncoder.encode("password"))
                .firstName("Cart")
                .lastName("Test")
                .role(User.Role.CUSTOMER)
                .build());

        Product product = productRepository.save(Product.builder()
                .name("Cart Test Product " + uid)
                .slug("cart-test-" + uid)
                .basePrice(BigDecimal.valueOf(49.99))
                .isActive(true)
                .build());

        variant = productVariantRepository.save(ProductVariant.builder()
                .product(product)
                .size("M")
                .color("Blue")
                .colorHex("#0000FF")
                .priceAdjustment(BigDecimal.ZERO)
                .stockQuantity(10)
                .sku("CART-TEST-" + uid)
                .isActive(true)
                .build());
    }

    @Test
    void getActiveCart_CreatesNewCartIfNoneExists() {
        CartResponse cart = cartService.getActiveCart(testUser.getId());
        assertThat(cart).isNotNull();
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotalItems()).isZero();
    }

    @Test
    void getActiveCart_ReturnsExistingCart() {
        CartResponse cart1 = cartService.getActiveCart(testUser.getId());
        CartResponse cart2 = cartService.getActiveCart(testUser.getId());
        assertThat(cart2.getId()).isEqualTo(cart1.getId());
    }

    @Test
    void addItem_AddsItemToCart() {
        AddCartItemRequest request = new AddCartItemRequest();
        request.setVariantId(variant.getId());
        request.setQuantity(2);

        CartResponse cart = cartService.addItem(testUser.getId(), request);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(cart.getItems().get(0).getVariantId()).isEqualTo(variant.getId());
    }

    @Test
    void addItem_ThrowsExceptionForInsufficientStock() {
        AddCartItemRequest request = new AddCartItemRequest();
        request.setVariantId(variant.getId());
        request.setQuantity(20);

        assertThrows(InsufficientStockException.class, () ->
                cartService.addItem(testUser.getId(), request));
    }

    @Test
    void addItem_ThrowsExceptionForInactiveVariant() {
        variant.setActive(false);
        productVariantRepository.save(variant);

        AddCartItemRequest request = new AddCartItemRequest();
        request.setVariantId(variant.getId());
        request.setQuantity(1);

        assertThrows(IllegalArgumentException.class, () ->
                cartService.addItem(testUser.getId(), request));
    }

    @Test
    void addItem_MergesWithExistingItem() {
        AddCartItemRequest request1 = new AddCartItemRequest();
        request1.setVariantId(variant.getId());
        request1.setQuantity(2);
        cartService.addItem(testUser.getId(), request1);

        AddCartItemRequest request2 = new AddCartItemRequest();
        request2.setVariantId(variant.getId());
        request2.setQuantity(3);
        CartResponse cart = cartService.addItem(testUser.getId(), request2);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(5);
    }

    @Test
    void updateItem_UpdatesQuantity() {
        AddCartItemRequest addReq = new AddCartItemRequest();
        addReq.setVariantId(variant.getId());
        addReq.setQuantity(2);
        CartResponse cart = cartService.addItem(testUser.getId(), addReq);

        Long itemId = cart.getItems().get(0).getId();
        UpdateCartItemRequest updateReq = new UpdateCartItemRequest();
        updateReq.setQuantity(5);

        CartResponse updated = cartService.updateItem(testUser.getId(), itemId, updateReq);
        assertThat(updated.getItems().get(0).getQuantity()).isEqualTo(5);
    }

    @Test
    void removeItem_RemovesItemFromCart() {
        AddCartItemRequest addReq = new AddCartItemRequest();
        addReq.setVariantId(variant.getId());
        addReq.setQuantity(1);
        CartResponse cart = cartService.addItem(testUser.getId(), addReq);

        Long itemId = cart.getItems().get(0).getId();
        CartResponse afterRemove = cartService.removeItem(testUser.getId(), itemId);

        assertThat(afterRemove.getItems()).isEmpty();
    }

    @Test
    void clearCart_EmptiesCart() {
        AddCartItemRequest addReq = new AddCartItemRequest();
        addReq.setVariantId(variant.getId());
        addReq.setQuantity(1);
        cartService.addItem(testUser.getId(), addReq);

        cartService.clearCart(testUser.getId());
        CartResponse cart = cartService.getActiveCart(testUser.getId());

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void addItem_ThrowsForNonexistentVariant() {
        AddCartItemRequest request = new AddCartItemRequest();
        request.setVariantId(99999L);
        request.setQuantity(1);

        assertThrows(ResourceNotFoundException.class, () ->
                cartService.addItem(testUser.getId(), request));
    }
}
