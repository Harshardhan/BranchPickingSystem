package com.example.demo;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final ProductKafkaProducer kafkaProducer;


    public ProductController(ProductService productService, ProductKafkaProducer kafkaProducer) {
        this.productService = productService;
		this.kafkaProducer = kafkaProducer;
    }

    // 🔹 Add Product
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody @Valid Product product)
            throws InValidProductException, ProductAlreadyExistsException {

        Product savedProduct = productService.addProduct(product);

        logger.info("🟢 Request to add product: {}", savedProduct.getProductName());

        // Send to Kafka
        kafkaProducer.sendProductEvent(savedProduct);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedProduct.getId())
            .toUri();

        return ResponseEntity.created(location).body(savedProduct);
    }
    // 🔹 Update Product
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) throws ProductAlreadyExistsException, InValidProductException, ProductNotFoundException {
        logger.info("🟡 Request to update product with ID: {}", id);
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    // 🔹 Delete Product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) throws ProductNotFoundException {
        logger.info("🔴 Request to delete product with ID: {}", id);
        String response = productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }

    // 🔹 Get Product By ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) throws ProductNotFoundException {
        logger.info("🔍 Request to fetch product with ID: {}", id);
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // 🔹 Get All Products
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() throws ProductNotFoundException {
        logger.info("📦 Request to fetch all products");
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // 🔹 Get Products by Name
    @GetMapping("/name/{productName}")
    public ResponseEntity<List<Product>> getProductsByName(@PathVariable("productName") String productName) throws ProductNotFoundException {
        logger.info("🔠 Request to fetch products with name: {}", productName);
        List<Product> products = productService.getProductsByName(productName);
        return ResponseEntity.ok(products);
    }

    // 🔹 Get Products by Category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable("category") String category) throws ProductNotFoundException {
        logger.info("🏷️ Request to fetch products in category: {}", category);
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    // 🔹 Get Products by Price Less Than
    @GetMapping("/price-less-than/{price}")
    public ResponseEntity<List<Product>> getProductsByPriceLessThan(@PathVariable("price") BigDecimal price) throws ProductNotFoundException {
        logger.info("💰 Request to fetch products with price < {}", price);
        List<Product> products = productService.getProductsByPriceLessThan(price);
        return ResponseEntity.ok(products);
    }

    // 🔹 Search Products by Keyword
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Product>> searchProducts(@PathVariable("keyword") String keyword) throws ProductNotFoundException {
        logger.info("🔎 Request to search products with keyword: {}", keyword);
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }
}
