package com.example.demo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;

@Service
@Transactional

public class ProductServiceImpl implements ProductService {

	private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

	private final ProductRepository productRepository;
	private final ProductKafkaProducer productKafkaProducer;

	public ProductServiceImpl(ProductRepository productRepository, ProductKafkaProducer productKafkaProducer) {
	    this.productRepository = productRepository;
	    this.productKafkaProducer = productKafkaProducer;
	}
    @Value("${kafka.topic}")
    private String topic;

	
    @Override
    @CachePut(value = "productById", key = "#result.id") // Cache newly added product by its ID
    @CacheEvict(value = "product", allEntries = true) // Clear product list cache
    public Product addProduct(Product product) throws InValidProductException, ProductAlreadyExistsException {

        if (product == null || product.getProductName() == null || product.getCategory() == null) {
            logger.warn("Invalid product: null values found.");
            throw new InValidProductException("Product name and category cannot be null or empty");
        }

        // Check for duplicates
        List<Product> existingProducts = productRepository.findByProductName(product.getProductName());
        boolean duplicate = existingProducts.stream()
            .anyMatch(p -> p.getCategory().equalsIgnoreCase(product.getCategory()));

        if (duplicate) {
            logger.warn("Product '{}' in category '{}' already exists.", product.getProductName(),
                    product.getCategory());
            throw new ProductAlreadyExistsException("A product with the same name and category already exists.");
        }

        // Save the product
        Product savedProduct = productRepository.save(product);
        logger.info("Product saved successfully with ID {}", savedProduct.getId());

        // Send Kafka notification (with error handling)
        String message = "New product available: " + savedProduct.getProductName();
        try {
        	productKafkaProducer.sendProductEvent(message);
            logger.info("Sent Kafka message: " + message);

        } catch (Exception e) {
            logger.error("Failed to send Kafka message: {}", message, e);
        }
        


        return savedProduct;
    }

	@Override
	@CacheEvict(value = "product", allEntries = true) // Clear product list cache
	@CachePut(value = "productById", key = "#id") // Update cache for individual product
	public Product updateProduct(Long id, Product productDetails)
			throws ProductAlreadyExistsException, InValidProductException, ProductNotFoundException {

		if (productDetails == null || productDetails.getProductName() == null || productDetails.getCategory() == null) {
			logger.warn("Product update request contains null or invalid details.");
			throw new InValidProductException("Product details (name, category) cannot be null.");
		}

		Product existingProduct = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));

		// Optional: Check if the updated name & category conflict with another existing
		// product
		List<Product> existingProducts = productRepository.findByProductName(productDetails.getProductName());
		boolean isDuplicate = existingProducts.stream()
				.anyMatch(p -> !p.getId().equals(id) && p.getCategory().equalsIgnoreCase(productDetails.getCategory()));

		if (isDuplicate) {
			logger.warn("Duplicate product detected with name '{}' and category '{}'.", productDetails.getProductName(),
					productDetails.getCategory());
			throw new ProductAlreadyExistsException("A product with the same name and category already exists.");
		}

		// ✅ Update fields
		existingProduct.setProductName(productDetails.getProductName());
		existingProduct.setCategory(productDetails.getCategory());
		existingProduct.setPrice(productDetails.getPrice());
		existingProduct.setAvailableQuantity(productDetails.getAvailableQuantity());
		existingProduct.setCurrencyCode(productDetails.getCurrencyCode());
		existingProduct.setDescription(productDetails.getDescription());
		existingProduct.setManufacturingDate(productDetails.getManufacturingDate());
		existingProduct.setExpiryDate(productDetails.getExpiryDate());

		Product updatedProduct = productRepository.save(existingProduct);
		logger.info("Product with ID {} updated successfully.", id);

		return updatedProduct;
	}

	@Override
	@CacheEvict(value = "productById", key = "#id") // Correct cache key for individual product
	public String deleteProduct(Long id) throws ProductNotFoundException {

		Optional<Product> product = productRepository.findById(id);
		if (product.isEmpty()) {
			logger.error("Attempted to delete non-existing product with ID {}", id);
			throw new ProductNotFoundException("Product not found with ID " + id);
		}

		productRepository.deleteById(id);
		logger.info("Product with ID {} deleted successfully.", id);
		return "Product deleted successfully";
	}

	// Get Product By Id
	@Override

	@CircuitBreaker(name = "ProductService", fallbackMethod = "fallbackGetProductById")
	@Retry(name = "ProductService")
	@RateLimiter(name = "ProductService")
	@Bulkhead(name = "ProductService")
	@Cacheable(value = "products", key = "'byId::' + #id")
	public Product getProductById(Long id) throws ProductNotFoundException {
		logger.info("🚨 DB call triggered: getProductById with ID: {}", id);

		return productRepository.findById(id).map(product -> {
			logger.info("✅ Product found with ID: {}", id);
			return product;
		}).orElseThrow(() -> {
			logger.error("❌ Product not found with ID: {}", id);
			return new ProductNotFoundException("Product with ID " + id + " not found in the database.");
		});
	}

	public Product fallbackGetProductById(Long id, Throwable t) {
	    logger.error("⚠️ Fallback triggered for getProductById({}) due to: {}", id, t.getMessage());
	    Product fallback = new Product();
	    fallback.setId(id);
	    fallback.setProductName("Unknown");
	    fallback.setDescription("Fallback: DB down or rate limited.");
	    fallback.setCategory("Unavailable");
	    return fallback;
	}

	// Get All Products
	@CircuitBreaker(name = "ProductService", fallbackMethod = "fallbackGetAllProducts")
	@Retry(name = "ProductService")
	@RateLimiter(name = "ProductService")
	@Bulkhead(name = "ProductService")
	@Cacheable(value = "productsAll", key = "'allProducts'")
	public List<Product> getAllProducts() throws ProductNotFoundException {
	    logger.info("📂 Cache MISS — fetching from DB");
	    logger.info("🚨 DB call triggered: getAllProducts");
	    List<Product> products = productRepository.findAll();
	    if (products.isEmpty()) {
	        throw new ProductNotFoundException("No products available.");
	    }
	    return products;
	}
	public List<Product> fallbackGetAllProducts(Throwable t) {
	    logger.error("⚠️ Fallback for getAllProducts due to: {}", t.getMessage());
	    Product fallback = new Product();
	    fallback.setDescription("No real products available. Fallback activated.");
	    return List.of(fallback);
	}
	@Override
	@Cacheable(value = "productsByName", key = "#productName")
	public List<Product> getProductsByName(String productName) throws ProductNotFoundException {
		List<Product> products = productRepository.findByProductName(productName);

		if (products.isEmpty()) {
			logger.warn("No products found with name '{}'", productName);
			throw new ProductNotFoundException("No products found with name: " + productName);
		}

		logger.info("Found {} products with name '{}'", products.size(), productName);
		return products;
	}

	@Override
	@CircuitBreaker(name = "ProductService", fallbackMethod = "fallbackByCategory")
	@Retry(name = "ProductService")
	@RateLimiter(name = "ProductService")
	@Bulkhead(name = "ProductService")
	@Cacheable(value = "products", key = "'byCategory::' + #category")
	public List<Product> getProductsByCategory(String category) throws ProductNotFoundException {
	    List<Product> products = productRepository.findByCategory(category);
	    if (products.isEmpty()) {
	        throw new ProductNotFoundException("No products found in category: " + category);
	    }
	    return products;
	}

	public List<Product> fallbackByCategory(String category, Throwable t) {
	    logger.warn("⚠️ Fallback triggered for getProductsByCategory('{}')", category);
	    Product fallback = new Product();
	    fallback.setProductName("Unavailable in " + category);
	    fallback.setCategory(category);
	    fallback.setDescription("Fallback response: Data not available");
	    return List.of(fallback);
	}
	@Override
	@Cacheable(value = "productsByPriceLess", key = "#price")
	public List<Product> getProductsByPriceLessThan(BigDecimal price) throws ProductNotFoundException {
		List<Product> products = productRepository.findByPriceLessThan(price);

		if (products.isEmpty()) {
			logger.warn("No products found with price less than {}", price);
			throw new ProductNotFoundException("No products found below price: " + price);
		}

		logger.info("Found {} products priced below {}", products.size(), price);
		return products;
	}

	@Override
	@Cacheable(value = "productsByKeyword", key = "#keyword")
	public List<Product> searchProducts(String keyword) throws ProductNotFoundException {
		List<Product> products = productRepository.findByProductNameContainingIgnoreCase(keyword);

		if (products.isEmpty()) {
			logger.warn("No products found with keyword '{}'", keyword);
			throw new ProductNotFoundException("No products found matching keyword: " + keyword);
		}

		logger.info("Found {} products containing keyword '{}'", products.size(), keyword);
		return products;
	}
}
