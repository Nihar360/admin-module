package com.ecommerce.admin.service;

import com.ecommerce.admin.dto.request.OrderStatusUpdateRequest;
import com.ecommerce.admin.dto.request.RefundRequest;
import com.ecommerce.admin.dto.response.*;
import com.ecommerce.admin.exception.BadRequestException;
import com.ecommerce.admin.exception.ResourceNotFoundException;
import com.ecommerce.admin.model.*;
import com.ecommerce.admin.model.enums.OrderStatus;
import com.ecommerce.admin.model.enums.RefundStatus;
import com.ecommerce.admin.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final OrderRefundRepository refundRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getOrders(
            OrderStatus status,
            String search,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orderPage = orderRepository.findByFilters(status, search, pageable);
        
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orderPage.getContent()) {
            responses.add(mapToOrderResponse(order));
        }
        
        log.info("Retrieved {} orders with status={}, search={}", 
                orderPage.getTotalElements(), status, search);
        
        return PageResponse.<OrderResponse>builder()
                .content(responses)
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .currentPage(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .build();
    }
    
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        log.info("Retrieved order details for order: {}", order.getOrderNumber());
        
        return mapToOrderDetailResponse(order);
    }
    
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request, Long adminId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();
        
        if (oldStatus == newStatus) {
            throw new BadRequestException("Order is already in " + newStatus + " status");
        }
        
        validateStatusTransition(oldStatus, newStatus);
        
        order.setStatus(newStatus);
        
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredDate(LocalDateTime.now());
        }
        
        orderRepository.save(order);
        
        OrderStatusHistory history = OrderStatusHistory.builder()
                .orderId(orderId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(adminId)
                .notes(request.getNotes())
                .build();
        statusHistoryRepository.save(history);
        
        log.info("Order {} status updated from {} to {} by admin {}", 
                order.getOrderNumber(), oldStatus, newStatus, adminId);
        
        return mapToOrderResponse(order);
    }
    
    @Transactional
    public void processRefund(Long orderId, RefundRequest request, Long adminId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.SHIPPED) {
            throw new BadRequestException("Can only refund delivered or shipped orders");
        }
        
        if (request.getRefundAmount().compareTo(order.getTotal()) > 0) {
            throw new BadRequestException("Refund amount cannot exceed order total");
        }
        
        refundRepository.findByOrderId(orderId).ifPresent(existingRefund -> {
            throw new BadRequestException("Refund request already exists for this order");
        });
        
        OrderRefund refund = OrderRefund.builder()
                .orderId(orderId)
                .refundAmount(request.getRefundAmount())
                .reason(request.getReason())
                .status(RefundStatus.APPROVED)
                .processedBy(adminId)
                .build();
        refundRepository.save(refund);
        
        order.setStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);
        
        OrderStatusHistory history = OrderStatusHistory.builder()
                .orderId(orderId)
                .oldStatus(order.getStatus())
                .newStatus(OrderStatus.REFUNDED)
                .changedBy(adminId)
                .notes("Refund processed: " + request.getReason())
                .build();
        statusHistoryRepository.save(history);
        
        log.info("Refund processed for order {} by admin {}, amount: {}", 
                order.getOrderNumber(), adminId, request.getRefundAmount());
    }
    
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryResponse> getOrderTimeline(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }
        
        List<OrderStatusHistory> history = statusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
        
        List<OrderStatusHistoryResponse> responses = new ArrayList<>();
        for (OrderStatusHistory record : history) {
            responses.add(OrderStatusHistoryResponse.builder()
                    .id(record.getId())
                    .orderId(record.getOrderId())
                    .oldStatus(record.getOldStatus())
                    .newStatus(record.getNewStatus())
                    .changedBy(record.getChangedBy())
                    .notes(record.getNotes())
                    .createdAt(record.getCreatedAt())
                    .build());
        }
        
        log.info("Retrieved timeline for order id: {}, {} entries", orderId, responses.size());
        
        return responses;
    }
    
    private void validateStatusTransition(OrderStatus oldStatus, OrderStatus newStatus) {
        if (oldStatus == OrderStatus.CANCELLED || oldStatus == OrderStatus.REFUNDED) {
            throw new BadRequestException("Cannot update status of cancelled or refunded orders");
        }
        
        if (newStatus == OrderStatus.CANCELLED && 
            (oldStatus == OrderStatus.SHIPPED || oldStatus == OrderStatus.DELIVERED)) {
            throw new BadRequestException("Cannot cancel shipped or delivered orders");
        }
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .customerName(order.getUser().getFullName())
                .customerEmail(order.getUser().getEmail())
                .subtotal(order.getSubtotal())
                .shipping(order.getShipping())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .itemCount(order.getItems() != null ? order.getItems().size() : 0)
                .orderDate(order.getOrderDate())
                .deliveredDate(order.getDeliveredDate())
                .createdAt(order.getCreatedAt())
                .build();
    }
    
    private OrderDetailResponse mapToOrderDetailResponse(Order order) {
        UserResponse customerResponse = UserResponse.builder()
                .id(order.getUser().getId())
                .fullName(order.getUser().getFullName())
                .email(order.getUser().getEmail())
                .mobile(order.getUser().getMobile())
                .role(order.getUser().getRole().name())
                .isActive(order.getUser().getIsActive())
                .profileImage(order.getUser().getProfileImage())
                .lastLogin(order.getUser().getLastLogin())
                .createdAt(order.getUser().getCreatedAt())
                .build();
        
        Address shippingAddr = order.getShippingAddress();
        AddressResponse addressResponse = AddressResponse.builder()
                .id(shippingAddr.getId())
                .fullName(shippingAddr.getFullName())
                .mobile(shippingAddr.getMobile())
                .addressLine1(shippingAddr.getAddressLine1())
                .addressLine2(shippingAddr.getAddressLine2())
                .city(shippingAddr.getCity())
                .state(shippingAddr.getState())
                .zipCode(shippingAddr.getZipCode())
                .country(shippingAddr.getCountry())
                .build();
        
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                itemResponses.add(OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .thumbnail(item.getProduct().getThumbnail())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .discount(item.getDiscount())
                        .total(item.getTotal())
                        .size(item.getSize())
                        .color(item.getColor())
                        .build());
            }
        }
        
        return OrderDetailResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .customer(customerResponse)
                .shippingAddress(addressResponse)
                .items(itemResponses)
                .subtotal(order.getSubtotal())
                .shipping(order.getShipping())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .couponCode(order.getCouponCode())
                .notes(order.getNotes())
                .orderDate(order.getOrderDate())
                .deliveredDate(order.getDeliveredDate())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class OrderStatusHistoryResponse {
    private Long id;
    private Long orderId;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private Long changedBy;
    private String notes;
    private LocalDateTime createdAt;
}
