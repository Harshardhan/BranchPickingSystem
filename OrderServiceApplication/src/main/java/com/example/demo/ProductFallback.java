package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.demo.excpetions.DownstreamServiceUnavailableException;

@Component
public class ProductFallback implements ProductClient {

    private static final Logger logger =
            LoggerFactory.getLogger(ProductFallback.class);

    @Override
    public Product getProductById(Long id) {
        logger.error("Product service is DOWN. Cannot fetch product {}", id);

        throw new DownstreamServiceUnavailableException(
                "Product service is unavailable. Order service running in SRE mode."
        );
    }
}
