package com.ecommerce.admin.repository;

import com.ecommerce.admin.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    List<OrderItem> findByProductId(Long productId);
    
    @Query("SELECT oi.product.id, oi.product.name, SUM(oi.quantity) as totalQuantity " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "AND o.status != 'CANCELLED' " +
           "GROUP BY oi.product.id, oi.product.name " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProducts(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
