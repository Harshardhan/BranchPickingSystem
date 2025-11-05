package com.example.demo;


import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order   {

	private Long id;
	private Long customerId;
	private Long productId;
	private String productName;
	private String description;
	private int quantity;
	private BigDecimal price;
	private String orderType;
	private String orderReference;
	private String paymentMethod;
	private String email;
	private String address;
	
	private OrderStatus orderStatus;


}
