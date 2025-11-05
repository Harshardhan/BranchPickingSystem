package com.example.demo;

import java.math.BigDecimal;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product {
	
	private Long id;
	
	private Long customerId;
	
	private Long orderId;
    private String productName;

    private String description;

    private BigDecimal price;

    private String currencyCode;

    private Integer availableQuantity;

    private String category;

    private LocalDate expiryDate;

    private LocalDate manufacturingDate;

    private String email;
    private boolean isActive = true;


}
