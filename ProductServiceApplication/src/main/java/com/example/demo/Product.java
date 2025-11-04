package com.example.demo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString

public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    private Long customerId;
    @NotNull
    @Column(length = 100, nullable = false, unique = true)
    private String productName;

    @NotNull
    @Size(min = 3, max = 50, message = "description must be 3 to 50 characters")
    @Column(nullable = false)
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private String currencyCode;

    private Integer availableQuantity;

    private String category;

    private LocalDate expiryDate;

    private LocalDate manufacturingDate;

    private String email;
    @Column(nullable = false)
    private boolean isActive = true;


    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

	@Override
	public String toString() {
		return "Product [id=" + id + ", customerId=" + customerId + ", productName=" + productName + ", description="
				+ description + ", price=" + price + ", currencyCode=" + currencyCode + ", availableQuantity="
				+ availableQuantity + ", category=" + category + ", expiryDate=" + expiryDate + ", manufacturingDate="
				+ manufacturingDate + ", email=" + email + ", isActive=" + isActive + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}

    
    
}
