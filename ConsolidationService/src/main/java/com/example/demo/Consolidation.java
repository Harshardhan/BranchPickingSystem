package com.example.demo;

import java.math.BigDecimal;


import java.time.LocalDate;
import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "consolidation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Consolidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long orderId;

    @Column(unique = true, nullable = false)
    private String orderReference;

    private int quantity;
    private String email;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)

    private OrderStatus orderStatus;
    private String optimisedItems;
    private String optimisedQuantity;
    private BigDecimal optimisedTotalAmount;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private boolean isPaid;
    private String orderType;
    private String deliveryAddress;
    private String paymentMethod;
    private String transactionId;
    private String currency;
    private String remarks;
    private boolean isConsolidated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters (generated automatically via Lombok or manually)
}
