package com.example.demo;

import java.time.LocalDateTime;
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
public class Analytics {
	private Long id;
	private Long customerId;
	
	private Long productId;
	
	private Long orderId;

	private String description;
	private String emailAddress;
	private String phoneNumber;

	
	private LocalDateTime orderPlacedAt;
	private LocalDateTime orderDeliveredAt;
	private Long deliveryDurationMinutes;
	private DeliveryStatus deliveryStatus;   // PENDING, IN_TRANSIT, DELIVERED, CANCELLED
	private String courierPartner;   // Optional, if logistics are external
	private String region;           // Optional, useful for SLA comparison



}
