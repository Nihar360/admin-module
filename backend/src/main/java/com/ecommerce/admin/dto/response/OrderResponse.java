package com.ecommerce.admin.dto.response;

import com.ecommerce.admin.model.enums.OrderStatus;
import com.ecommerce.admin.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    
    private String customerName;
    private String customerEmail;
    
    private BigDecimal subtotal;
    private BigDecimal shipping;
    private BigDecimal discount;
    private BigDecimal total;
    
    private Integer itemCount;
    
    private LocalDateTime orderDate;
    private LocalDateTime deliveredDate;
    private LocalDateTime createdAt;
}
