package com.example.demo;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "analytics")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Analytics {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotNull(message ="customerId is required")
	private Long customerId;
	
	@NotNull(message ="productId is required")
	private Long productId;
	
	@NotNull(message ="orderId is required")
	private Long orderId;

	private String description;
	private String emailAddress;
	private String phoneNumber;

	
	private LocalDateTime orderPlacedAt;
	private LocalDateTime orderDeliveredAt;
    @PositiveOrZero(message = "Delivery duration must be positive")
	private Long deliveryDurationMinutes;
	private DeliveryStatus deliveryStatus;   // PENDING, IN_TRANSIT, DELIVERED, CANCELLED
	private String courierPartner;   // Optional, if logistics are external
	private String region;           // Optional, useful for SLA comparison

	
	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@Override
	public String toString() {
		return "Analytics [id=" + id + ", customerId=" + customerId + ", productId=" + productId + ", orderId="
				+ orderId + ", description=" + description + ", emailAddress=" + emailAddress + ", phoneNumber="
				+ phoneNumber + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
