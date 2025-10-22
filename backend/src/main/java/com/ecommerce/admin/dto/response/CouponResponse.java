package com.ecommerce.admin.dto.response;

import com.ecommerce.admin.model.enums.CouponType;
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
public class CouponResponse {
    
    private Long id;
    private String code;
    private CouponType type;
    private BigDecimal value;
    private BigDecimal minPurchase;
    private BigDecimal maxDiscount;
    private Integer usageLimit;
    private Integer usageCount;
    private LocalDateTime expiresAt;
    private Boolean isActive;
    private Boolean isExpired;
    private LocalDateTime createdAt;
}
