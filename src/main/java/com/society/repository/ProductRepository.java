package com.society.repository;

import com.society.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Get all active products
    List<Product> findByIsActiveTrueOrderByCreatedAtDesc();

    // Get products by user
    List<Product> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId);

    // Get products by category
    List<Product> findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(String category);

    // Search by title
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY p.createdAt DESC")
    List<Product> searchByTitle(String query);

    // Filter by price range
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.createdAt DESC")
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    // Get all products (for admin)
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findAllOrderByCreatedAtDesc();

    // Count by user
    Long countByUserId(Long userId);
}
