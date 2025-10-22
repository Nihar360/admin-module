package com.ecommerce.admin.dto.response;

import com.ecommerce.admin.model.enums.OrderStatus;
import com.ecommerce.admin.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    
    private UserResponse customer;
    private AddressResponse shippingAddress;
    
    private List<OrderItemResponse> items;
    
    private BigDecimal subtotal;
    private BigDecimal shipping;
    private BigDecimal discount;
    private BigDecimal total;
    
    private String couponCode;
    private String notes;
    
    private LocalDateTime orderDate;
    private LocalDateTime deliveredDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

