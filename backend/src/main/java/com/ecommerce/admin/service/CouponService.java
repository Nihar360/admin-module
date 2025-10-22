package com.ecommerce.admin.service;

import com.ecommerce.admin.dto.request.CouponCreateRequest;
import com.ecommerce.admin.dto.response.CouponResponse;
import com.ecommerce.admin.exception.BadRequestException;
import com.ecommerce.admin.exception.ResourceNotFoundException;
import com.ecommerce.admin.exception.ValidationException;
import com.ecommerce.admin.model.Coupon;
import com.ecommerce.admin.model.enums.CouponType;
import com.ecommerce.admin.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {
    
    private final CouponRepository couponRepository;
    
    @Transactional(readOnly = true)
    public List<CouponResponse> getCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        
        List<CouponResponse> responses = new ArrayList<>();
        for (Coupon coupon : coupons) {
            responses.add(mapToCouponResponse(coupon));
        }
        
        log.info("Retrieved {} coupons", responses.size());
        
        return responses;
    }
    
    @Transactional(readOnly = true)
    public CouponResponse getCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        
        log.info("Retrieved coupon: {}", coupon.getCode());
        
        return mapToCouponResponse(coupon);
    }
    
    @Transactional
    public CouponResponse createCoupon(CouponCreateRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new ValidationException("Coupon with code " + request.getCode() + " already exists");
        }
        
        if (request.getType() == CouponType.PERCENTAGE && 
            request.getValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ValidationException("Percentage discount cannot exceed 100%");
        }
        
        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .type(request.getType())
                .value(request.getValue())
                .minPurchase(request.getMinPurchase() != null ? request.getMinPurchase() : BigDecimal.ZERO)
                .maxDiscount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit())
                .usageCount(0)
                .expiresAt(request.getExpiresAt())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        
        coupon = couponRepository.save(coupon);
        
        log.info("Created new coupon: {} of type {}", coupon.getCode(), coupon.getType());
        
        return mapToCouponResponse(coupon);
    }
    
    @Transactional
    public CouponResponse updateCoupon(Long id, CouponCreateRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        
        if (!coupon.getCode().equals(request.getCode()) && 
            couponRepository.existsByCode(request.getCode())) {
            throw new ValidationException("Coupon with code " + request.getCode() + " already exists");
        }
        
        if (request.getType() == CouponType.PERCENTAGE && 
            request.getValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ValidationException("Percentage discount cannot exceed 100%");
        }
        
        coupon.setCode(request.getCode().toUpperCase());
        coupon.setType(request.getType());
        coupon.setValue(request.getValue());
        coupon.setMinPurchase(request.getMinPurchase() != null ? request.getMinPurchase() : BigDecimal.ZERO);
        coupon.setMaxDiscount(request.getMaxDiscount());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setExpiresAt(request.getExpiresAt());
        coupon.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        coupon = couponRepository.save(coupon);
        
        log.info("Updated coupon: {}", coupon.getCode());
        
        return mapToCouponResponse(coupon);
    }
    
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        
        coupon.setIsActive(false);
        couponRepository.save(coupon);
        
        log.info("Deactivated coupon: {}", coupon.getCode());
    }
    
    @Transactional(readOnly = true)
    public CouponValidationResponse validateCoupon(String code, BigDecimal orderTotal) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));
        
        if (!coupon.getIsActive()) {
            throw new BadRequestException("Coupon is not active");
        }
        
        if (coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Coupon has expired");
        }
        
        if (coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new BadRequestException("Coupon usage limit reached");
        }
        
        if (orderTotal.compareTo(coupon.getMinPurchase()) < 0) {
            throw new BadRequestException("Order total must be at least " + 
                    coupon.getMinPurchase() + " to use this coupon");
        }
        
        BigDecimal discountAmount = calculateDiscount(coupon, orderTotal);
        
        log.info("Validated coupon: {}, discount: {}", coupon.getCode(), discountAmount);
        
        return CouponValidationResponse.builder()
                .valid(true)
                .discountAmount(discountAmount)
                .couponCode(coupon.getCode())
                .message("Coupon applied successfully")
                .build();
    }
    
    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderTotal) {
        BigDecimal discount;
        
        if (coupon.getType() == CouponType.PERCENTAGE) {
            discount = orderTotal.multiply(coupon.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            
            if (coupon.getMaxDiscount() != null && 
                discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
        } else {
            discount = coupon.getValue();
            
            if (discount.compareTo(orderTotal) > 0) {
                discount = orderTotal;
            }
        }
        
        return discount;
    }
    
    private CouponResponse mapToCouponResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .type(coupon.getType())
                .value(coupon.getValue())
                .minPurchase(coupon.getMinPurchase())
                .maxDiscount(coupon.getMaxDiscount())
                .usageLimit(coupon.getUsageLimit())
                .usageCount(coupon.getUsageCount())
                .expiresAt(coupon.getExpiresAt())
                .isActive(coupon.getIsActive())
                .isExpired(coupon.getExpiresAt().isBefore(LocalDateTime.now()))
                .createdAt(coupon.getCreatedAt())
                .build();
    }
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class CouponValidationResponse {
    private Boolean valid;
    private BigDecimal discountAmount;
    private String couponCode;
    private String message;
}
