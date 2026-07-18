package com.clothingshop.config;

import com.clothingshop.product.entity.Category;
import com.clothingshop.product.entity.Product;
import com.clothingshop.product.entity.ProductVariant;
import com.clothingshop.product.repository.CategoryRepository;
import com.clothingshop.product.repository.ProductRepository;
import com.clothingshop.product.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeedDataRunner implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category tops = categoryRepository.save(
                Category.builder().name("Tops").slug("tops").description("Shirts, t-shirts, and blouses").build());
        Category bottoms = categoryRepository.save(
                Category.builder().name("Bottoms").slug("bottoms").description("Pants, shorts, and skirts").build());
        Category outerwear = categoryRepository.save(
                Category.builder().name("Outerwear").slug("outerwear").description("Jackets and coats").build());
        Category accessories = categoryRepository.save(
                Category.builder().name("Accessories").slug("accessories").description("Hats, bags, and belts").build());

        Product classicTee = createProduct("Classic Cotton T-Shirt", "classic-cotton-t-shirt",
                "A comfortable classic cotton t-shirt for everyday wear.", BigDecimal.valueOf(29.99),
                "/images/classic-tee.jpg", tops);
        createVariants(classicTee, List.of("S", "M", "L", "XL"), "White", "#FFFFFF", 50);
        createVariants(classicTee, List.of("S", "M", "L", "XL"), "Black", "#000000", 40);
        createVariants(classicTee, List.of("S", "M", "L", "XL"), "Navy", "#000080", 30);

        Product denimJacket = createProduct("Slim Fit Denim Jacket", "slim-fit-denim-jacket",
                "A stylish slim fit denim jacket with a modern cut.", BigDecimal.valueOf(89.99),
                "/images/denim-jacket.jpg", outerwear);
        createVariants(denimJacket, List.of("M", "L", "XL"), "Blue", "#1E3A5F", 20);
        createVariants(denimJacket, List.of("M", "L", "XL"), "Black", "#000000", 15);

        Product chinos = createProduct("Premium Chino Pants", "premium-chino-pants",
                "High-quality chino pants for a smart casual look.", BigDecimal.valueOf(59.99),
                "/images/chinos.jpg", bottoms);
        createVariants(chinos, List.of("30", "32", "34", "36"), "Khaki", "#C3B091", 35);
        createVariants(chinos, List.of("30", "32", "34", "36"), "Navy", "#000080", 25);

        Product baseballCap = createProduct("Cotton Baseball Cap", "cotton-baseball-cap",
                "A classic cotton baseball cap with adjustable strap.", BigDecimal.valueOf(19.99),
                "/images/baseball-cap.jpg", accessories);
        createVariants(baseballCap, List.of("One Size"), "Black", "#000000", 100);
        createVariants(baseballCap, List.of("One Size"), "Red", "#FF0000", 80);
    }

    private Product createProduct(String name, String slug, String description,
                                   BigDecimal price, String imageUrl, Category category) {
        return productRepository.save(Product.builder()
                .name(name).slug(slug).description(description)
                .basePrice(price).imageUrl(imageUrl).category(category)
                .isActive(true).build());
    }

    private void createVariants(Product product, List<String> sizes, String color, String colorHex, int stock) {
        int index = 1;
        for (String size : sizes) {
            productVariantRepository.save(ProductVariant.builder()
                    .product(product)
                    .size(size)
                    .color(color)
                    .colorHex(colorHex)
                    .priceAdjustment(BigDecimal.ZERO)
                    .stockQuantity(stock)
                    .sku(product.getSlug().toUpperCase() + "-" + color.toUpperCase() + "-" + size)
                    .isActive(true)
                    .build());
            index++;
        }
    }
}
