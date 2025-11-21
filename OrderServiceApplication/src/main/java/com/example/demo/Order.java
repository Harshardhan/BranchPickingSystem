package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ✔ Set by JWT, still need @NotNull to enforce after @Valid
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)

	@Column(nullable = false)
	private Long customerId;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)

	private String userName;

	// ✔ REQUIRED
	@NotNull(message = "Product ID is required")
	private Long productId;

	private String productName;

	private String description;

	// ✔ VALIDATE quantity > 0
	@Min(value = 1, message = "Quantity must be at least 1")
	private int quantity;

	// ✔ VALIDATE price
	@NotNull(message = "Price is required")
	@DecimalMin(value = "1.0", message = "Price must be greater than 0")
	private BigDecimal price;

	// ✔ MUST NOT BE BLANK
	@NotBlank(message = "Order type is required")
	private String orderType;

	@Column(unique = true)
	private String orderReference;

	// ✔ ENUM VALIDATION
	@NotNull(message = "Payment method is required")
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	// ✔ EMAIL VALIDATION
	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Address is required")
	private String address;

	@NotBlank(message = "Mobile number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
	private String mobileNumber;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
