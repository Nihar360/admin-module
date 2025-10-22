package com.ecommerce.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String fullName;
    private String email;
    private String mobile;
    private String role;
    private Boolean isActive;
    private String profileImage;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    
    private Long totalOrders;
    private Long totalSpent;
}
