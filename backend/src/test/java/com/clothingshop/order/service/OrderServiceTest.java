package com.clothingshop.order.service;

import com.clothingshop.cart.dto.AddCartItemRequest;
import com.clothingshop.cart.service.CartService;
import com.clothingshop.common.exception.InsufficientStockException;
import com.clothingshop.common.exception.PriceMismatchException;
import com.clothingshop.order.dto.CheckoutRequest;
import com.clothingshop.order.dto.OrderResponse;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

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
    private CheckoutRequest checkoutRequest;

    @BeforeEach
    void setUp() {
        String uid = String.valueOf(System.currentTimeMillis());
        testUser = userRepository.save(User.builder()
                .email("order-test-" + uid + "@test.com")
                .password(passwordEncoder.encode("password"))
                .firstName("Order")
                .lastName("Test")
                .role(User.Role.CUSTOMER)
                .build());

        Product product = productRepository.save(Product.builder()
                .name("Order Test Product " + uid)
                .slug("order-test-" + uid)
                .basePrice(BigDecimal.valueOf(29.99))
                .isActive(true)
                .build());

        variant = productVariantRepository.save(ProductVariant.builder()
                .product(product)
                .size("L")
                .color("Red")
                .colorHex("#FF0000")
                .priceAdjustment(BigDecimal.valueOf(5.00))
                .stockQuantity(5)
                .sku("ORDER-TEST-" + uid)
                .isActive(true)
                .build());

        checkoutRequest = new CheckoutRequest();
        checkoutRequest.setShippingAddress("456 Test Ave, Testville, 67890");

        AddCartItemRequest addReq = new AddCartItemRequest();
        addReq.setVariantId(variant.getId());
        addReq.setQuantity(2);
        cartService.addItem(testUser.getId(), addReq);
    }

    @Test
    void checkout_CreatesOrderSuccessfully() {
        OrderResponse order = orderService.checkout(testUser.getId(), checkoutRequest);

        assertThat(order).isNotNull();
        assertThat(order.getStatus()).isEqualTo("PENDING");
        assertThat(order.getPaymentStatus()).isEqualTo("UNPAID");
        assertThat(order.getShippingAddress()).isEqualTo("456 Test Ave, Testville, 67890");
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void checkout_DecrementsStock() {
        int stockBefore = variant.getStockQuantity();
        orderService.checkout(testUser.getId(), checkoutRequest);

        ProductVariant afterVariant = productVariantRepository.findById(variant.getId()).orElseThrow();
        assertThat(afterVariant.getStockQuantity()).isEqualTo(stockBefore - 2);
    }

    @Test
    void checkout_ThrowsForInsufficientStock() {
        variant.setStockQuantity(0);
        productVariantRepository.save(variant);

        assertThrows(InsufficientStockException.class, () ->
                orderService.checkout(testUser.getId(), checkoutRequest));
    }

    @Test
    void checkout_ThrowsForEmptyCart() {
        cartService.clearCart(testUser.getId());
        assertThrows(IllegalStateException.class, () ->
                orderService.checkout(testUser.getId(), checkoutRequest));
    }

    @Test
    void getUserOrders_ReturnsOrders() {
        orderService.checkout(testUser.getId(), checkoutRequest);
        List<OrderResponse> orders = orderService.getUserOrders(testUser.getId());
        assertThat(orders).hasSize(1);
    }

    @Test
    void getOrder_ReturnsOrder() {
        OrderResponse created = orderService.checkout(testUser.getId(), checkoutRequest);
        OrderResponse fetched = orderService.getOrder(testUser.getId(), created.getId());
        assertThat(fetched.getId()).isEqualTo(created.getId());
    }

    @Test
    void checkout_ConvertsCart() {
        orderService.checkout(testUser.getId(), checkoutRequest);
        assertThrows(IllegalStateException.class, () ->
                orderService.checkout(testUser.getId(), checkoutRequest));
    }
}
