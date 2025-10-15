package com.example.demo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PaymentResponse implements Serializable {

    private String message;

	private Long userId;

	private Long orderId;
	
	
	private String username;
	
	private PaymentMethod method;
	
	private PaymentStatus status;

	private BigDecimal amount;
	private String currencyCode; // e.g., "USD", "INR"
	
	private LocalDateTime paymentTimestamp;
	
	private String emailId;
	
	private String mobileNumber;
	
	

}
