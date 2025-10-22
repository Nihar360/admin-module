package com.ecommerce.admin.service;

import com.ecommerce.admin.dto.response.OrderResponse;
import com.ecommerce.admin.dto.response.PageResponse;
import com.ecommerce.admin.dto.response.UserResponse;
import com.ecommerce.admin.exception.ResourceNotFoundException;
import com.ecommerce.admin.model.Order;
import com.ecommerce.admin.model.User;
import com.ecommerce.admin.repository.OrderRepository;
import com.ecommerce.admin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsers(
            String search,
            Boolean isActive,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage = userRepository.findCustomers(search, isActive, pageable);
        
        List<UserResponse> responses = new ArrayList<>();
        for (User user : userPage.getContent()) {
            responses.add(mapToUserResponse(user, false));
        }
        
        log.info("Retrieved {} users with search={}, isActive={}", 
                userPage.getTotalElements(), search, isActive);
        
        return PageResponse.<UserResponse>builder()
                .content(responses)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .currentPage(userPage.getNumber())
                .pageSize(userPage.getSize())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        log.info("Retrieved user: {}", user.getEmail());
        
        return mapToUserResponse(user, true);
    }
    
    @Transactional
    public UserResponse updateUserStatus(Long id, Boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setIsActive(isActive);
        user = userRepository.save(user);
        
        log.info("Updated user status for {}: isActive={}", user.getEmail(), isActive);
        
        return mapToUserResponse(user, false);
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        
        List<Order> orders = orderRepository.findByUserId(userId);
        
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(mapToOrderResponse(order));
        }
        
        log.info("Retrieved {} orders for user id: {}", responses.size(), userId);
        
        return responses;
    }
    
    private UserResponse mapToUserResponse(User user, boolean includeStats) {
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .profileImage(user.getProfileImage())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt());
        
        if (includeStats) {
            List<Order> userOrders = orderRepository.findByUserId(user.getId());
            Long totalOrders = (long) userOrders.size();
            
            Long totalSpent = userOrders.stream()
                    .map(Order::getTotal)
                    .map(BigDecimal::longValue)
                    .reduce(0L, Long::sum);
            
            builder.totalOrders(totalOrders);
            builder.totalSpent(totalSpent);
        }
        
        return builder.build();
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
}
