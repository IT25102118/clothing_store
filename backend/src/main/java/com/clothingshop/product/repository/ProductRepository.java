package com.clothingshop.product.repository;

import com.clothingshop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlugAndIsActiveTrue(String slug);

    Optional<Product> findByIdAndIsActiveTrue(Long id);

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> searchByName(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category.id = :categoryId AND " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> findByCategoryIdAndSearch(@Param("categoryId") Long categoryId,
                                            @Param("search") String search,
                                            Pageable pageable);
}
