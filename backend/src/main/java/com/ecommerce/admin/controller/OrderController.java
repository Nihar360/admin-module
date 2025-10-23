package com.ecommerce.admin.controller;

import com.ecommerce.admin.dto.request.OrderStatusUpdateRequest;
import com.ecommerce.admin.dto.request.RefundRequest;
import com.ecommerce.admin.dto.response.ApiResponse;
import com.ecommerce.admin.dto.response.OrderDetailResponse;
import com.ecommerce.admin.dto.response.OrderResponse;
import com.ecommerce.admin.dto.response.PageResponse;
import com.ecommerce.admin.model.enums.OrderStatus;
import com.ecommerce.admin.security.SecurityUser;
import com.ecommerce.admin.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Slf4j
@Validated
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching orders with status={}, search={}, page={}, size={}", status, search, page, size);
        PageResponse<OrderResponse> orders = orderService.getOrders(status, search, page, size);
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getOrderDetails(@PathVariable Long id) {
        log.info("Fetching order details for id: {}", id);
        OrderDetailResponse order = orderService.getOrderDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Order details retrieved successfully", order));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request,
            @AuthenticationPrincipal SecurityUser currentUser) {
        log.info("Updating order {} status to {} by admin {}", id, request.getStatus(), currentUser.getId());
        OrderResponse order = orderService.updateOrderStatus(id, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", order));
    }
    
    @PostMapping("/{id}/refund")
    public ResponseEntity<ApiResponse> processRefund(
            @PathVariable Long id,
            @Valid @RequestBody RefundRequest request,
            @AuthenticationPrincipal SecurityUser currentUser) {
        log.info("Processing refund for order {} by admin {}", id, currentUser.getId());
        orderService.processRefund(id, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Refund processed successfully"));
    }
    
    @GetMapping("/{id}/timeline")
    public ResponseEntity<ApiResponse> getOrderTimeline(@PathVariable Long id) {
        log.info("Fetching timeline for order: {}", id);
        var timeline = orderService.getOrderTimeline(id);
        return ResponseEntity.ok(ApiResponse.success("Order timeline retrieved successfully", timeline));
    }
}
