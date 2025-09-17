package com.example.demo;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    // 🔹 Add a new product
    Product addProduct(Product product) throws  InValidProductException, ProductAlreadyExistsException;

    // 🔹 Update an existing product
    Product updateProduct(Long id, Product productDetails)
            throws ProductAlreadyExistsException, InValidProductException;

    // 🔹 Delete a product by ID
    String deleteProduct(Long id) throws ProductNotFoundException;

    // 🔹 Get a product by ID
    Product getProductById(Long id) throws ProductNotFoundException;

    // 🔹 Get all products
    List<Product> getAllProducts();

    // 🔹 Search by product name
    List<Product> getProductsByName(String productName) throws ProductNotFoundException;

    // 🔹 Filter by category
    List<Product> getProductsByCategory(String category) throws ProductNotFoundException;

    // 🔹 Search products with price less than a value
    List<Product> getProductsByPriceLessThan(BigDecimal price) throws ProductNotFoundException;

    // 🔹 Search by keyword in product name
    List<Product> searchProducts(String keyword) throws ProductNotFoundException;
}
