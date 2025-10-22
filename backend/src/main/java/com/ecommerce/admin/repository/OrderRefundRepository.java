package com.ecommerce.admin.repository;

import com.ecommerce.admin.model.OrderRefund;
import com.ecommerce.admin.model.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRefundRepository extends JpaRepository<OrderRefund, Long> {
    
    Optional<OrderRefund> findByOrderId(Long orderId);
    
    List<OrderRefund> findByStatus(RefundStatus status);
    
    List<OrderRefund> findByProcessedBy(Long processedBy);
}
