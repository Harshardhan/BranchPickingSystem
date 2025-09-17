package com.example.demo;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 🔹 1. Find product by exact name match
    List<Product> findByProductName(String productName);

    // 🔹 2. Find products by category (e.g., Electronics, Clothing, etc.)
    List<Product> findByCategory(String category);

    // 🔹 3. Find products with price less than a value
    List<Product> findByPriceLessThan(BigDecimal price);

    // 🔹 4. Find products with price between two values
    List<Product> findByPriceBetween(BigDecimal min, Double max);

    // 🔹 5. Find products by name containing a keyword (like search)
    List<Product> findByProductNameContainingIgnoreCase(String keyword);

    // 🔹 6. Find products sorted by price descending
    List<Product> findAllByOrderByPriceDesc();

    // 🔹 7. Find products by category and sorted by price ascending
    List<Product> findByCategoryOrderByPriceAsc(String category);
}
