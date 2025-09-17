package com.example.demo;

import java.io.Serializable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequest implements Serializable{

    private Long customerId;
    private Long orderId;
    private Long productId;
    private String message;
    private String productName;
    private String description;
    private int quantity;
    private BigDecimal price;
    private String orderType;
    private String orderReference;
    private PaymentMethod paymentMethod;
    private String email;
    private String address;

    private NotificationType type;

    // Optional: Remove these two if they’re only meant to be controlled by NotificationService
    // private LocalDateTime sentAt;
    // private boolean sent;
}
