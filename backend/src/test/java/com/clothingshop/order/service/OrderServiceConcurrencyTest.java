package com.clothingshop.order.service;

import com.clothingshop.cart.dto.AddCartItemRequest;
import com.clothingshop.cart.service.CartService;
import com.clothingshop.common.exception.InsufficientStockException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderServiceConcurrencyTest {

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
    private CheckoutRequest checkoutRequest;

    @BeforeEach
    void setUp() {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());

        testUser = userRepository.save(User.builder()
                .email("concurrency-" + uniqueSuffix + "@test.com")
                .password(passwordEncoder.encode("password"))
                .firstName("Concurrency")
                .lastName("Test")
                .role(User.Role.CUSTOMER)
                .build());

        Product product = productRepository.save(Product.builder()
                .name("Concurrency Test Product " + uniqueSuffix)
                .slug("concurrency-test-product-" + uniqueSuffix)
                .description("Test product for concurrency testing")
                .basePrice(BigDecimal.valueOf(29.99))
                .isActive(true)
                .build());

        ProductVariant variant = productVariantRepository.save(ProductVariant.builder()
                .product(product)
                .size("M")
                .color("Black")
                .colorHex("#000000")
                .priceAdjustment(BigDecimal.ZERO)
                .stockQuantity(1)
                .sku("CONC-" + uniqueSuffix + "-BLACK-M")
                .isActive(true)
                .build());

        checkoutRequest = new CheckoutRequest();
        checkoutRequest.setShippingAddress("123 Test St, Test City, 12345");

        cartService.addItem(testUser.getId(), new AddCartItemRequest(variant.getId(), 1));
    }

    @Test
    void simultaneousCheckout_OnlyOneShouldSucceed() throws Exception {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                try {
                    OrderResponse order = orderService.checkout(testUser.getId(), checkoutRequest);
                    return "SUCCESS:" + order.getId();
                } catch (InsufficientStockException e) {
                    return "INSUFFICIENT_STOCK";
                } catch (Exception e) {
                    return "FAILED:" + e.getClass().getSimpleName() + ":" + e.getMessage();
                }
            });
        }

        List<Future<String>> futures = executor.invokeAll(tasks);
        executor.shutdown();

        List<String> results = new ArrayList<>();
        for (Future<String> future : futures) {
            results.add(future.get());
        }

        long successCount = results.stream().filter(r -> r.startsWith("SUCCESS")).count();
        long failureCount = results.stream().filter(r -> !r.startsWith("SUCCESS")).count();

        assertThat(successCount).isEqualTo(1);
        assertThat(failureCount).isEqualTo(1);

        String failureResult = results.stream().filter(r -> !r.startsWith("SUCCESS")).findFirst().orElse("");
        assertThat(failureResult).contains("INSUFFICIENT_STOCK");
    }
}
