package com.ecommerce.admin.repository;

import com.ecommerce.admin.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    Optional<Coupon> findByCode(String code);
    
    List<Coupon> findByIsActive(Boolean isActive);
    
    List<Coupon> findByExpiresAtBefore(LocalDateTime dateTime);
    
    boolean existsByCode(String code);
}
