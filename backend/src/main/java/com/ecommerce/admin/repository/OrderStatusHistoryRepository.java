package com.ecommerce.admin.repository;

import com.ecommerce.admin.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    
    List<OrderStatusHistory> findByOrderIdOrderByCreatedAtAsc(Long orderId);
    
    List<OrderStatusHistory> findByChangedBy(Long changedBy);
}
