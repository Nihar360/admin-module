package com.ecommerce.admin.controller;

import com.ecommerce.admin.dto.request.CouponCreateRequest;
import com.ecommerce.admin.dto.response.ApiResponse;
import com.ecommerce.admin.dto.response.CouponResponse;
import com.ecommerce.admin.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/coupons")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CouponController {
    
    private final CouponService couponService;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getCoupons() {
        log.info("Fetching all coupons");
        List<CouponResponse> coupons = couponService.getCoupons();
        return ResponseEntity.ok(ApiResponse.success("Coupons retrieved successfully", coupons));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCoupon(@PathVariable Long id) {
        log.info("Fetching coupon with id: {}", id);
        CouponResponse coupon = couponService.getCoupon(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon retrieved successfully", coupon));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        log.info("Creating new coupon: {}", request.getCode());
        CouponResponse coupon = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Coupon created successfully", coupon));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody CouponCreateRequest request) {
        log.info("Updating coupon with id: {}", id);
        CouponResponse coupon = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(ApiResponse.success("Coupon updated successfully", coupon));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCoupon(@PathVariable Long id) {
        log.info("Deleting coupon with id: {}", id);
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted successfully"));
    }
}
