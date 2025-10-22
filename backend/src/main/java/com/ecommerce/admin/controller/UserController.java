package com.ecommerce.admin.controller;

import com.ecommerce.admin.dto.response.ApiResponse;
import com.ecommerce.admin.dto.response.OrderResponse;
import com.ecommerce.admin.dto.response.PageResponse;
import com.ecommerce.admin.dto.response.UserResponse;
import com.ecommerce.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching users with search={}, isActive={}, page={}, size={}", search, isActive, page, size);
        PageResponse<UserResponse> users = userService.getUsers(search, isActive, page, size);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        UserResponse user = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        Boolean isActive = request.get("isActive");
        log.info("Updating user {} status to: {}", id, isActive);
        UserResponse user = userService.updateUserStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", user));
    }
    
    @GetMapping("/{id}/orders")
    public ResponseEntity<ApiResponse> getUserOrders(@PathVariable Long id) {
        log.info("Fetching orders for user: {}", id);
        List<OrderResponse> orders = userService.getUserOrders(id);
        return ResponseEntity.ok(ApiResponse.success("User orders retrieved successfully", orders));
    }
}
