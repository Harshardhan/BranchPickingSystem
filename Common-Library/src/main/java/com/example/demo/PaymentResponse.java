package com.example.demo;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PaymentResponse   {

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
