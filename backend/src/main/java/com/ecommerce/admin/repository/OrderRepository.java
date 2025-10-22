package com.ecommerce.admin.repository;

import com.ecommerce.admin.model.Order;
import com.ecommerce.admin.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUserId(Long userId);
    
    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:search IS NULL OR " +
           "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.user.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Order> findByFilters(
        @Param("status") OrderStatus status,
        @Param("search") String search,
        Pageable pageable
    );
    
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "AND o.status != 'CANCELLED'")
    BigDecimal getTotalRevenue(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    Long countByStatus(OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE " +
           "o.createdAt BETWEEN :startDate AND :endDate AND " +
           "o.status != 'CANCELLED'")
    Long countSuccessfulOrders(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
