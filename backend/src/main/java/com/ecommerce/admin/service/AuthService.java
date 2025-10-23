package com.ecommerce.admin.service;

import com.ecommerce.admin.dto.request.LoginRequest;
import com.ecommerce.admin.dto.request.PasswordChangeRequest;
import com.ecommerce.admin.dto.response.LoginResponse;
import com.ecommerce.admin.dto.response.UserResponse;
import com.ecommerce.admin.exception.UnauthorizedException;
import com.ecommerce.admin.exception.ValidationException;
import com.ecommerce.admin.model.User;
import com.ecommerce.admin.model.enums.UserRole;
import com.ecommerce.admin.repository.UserRepository;
import com.ecommerce.admin.security.JwtTokenProvider;
import com.ecommerce.admin.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityUser userDetails = (SecurityUser) authentication.getPrincipal();
        
        if (!userDetails.getRole().equals(UserRole.ADMIN.name())) {
            throw new UnauthorizedException("Access denied. Admin privileges required");
        }
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        String token = tokenProvider.generateToken(authentication);
        
        log.info("Admin {} logged in successfully", userDetails.getEmail());
        
        // Build nested UserResponse for frontend compatibility
        UserResponse userResponse = UserResponse.builder()
                .id(userDetails.getId())
                .fullName(userDetails.getFullName())
                .email(userDetails.getEmail())
                .mobile(user.getMobile())
                .role(userDetails.getRole())
                .isActive(user.getIsActive())
                .profileImage(user.getProfileImage())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
        
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .user(userResponse)  // Nested user object
                .build();
    }
    
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .profileImage(user.getProfileImage())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ValidationException("Current password is incorrect");
        }
        
        // Validate new password (optional but recommended)
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new ValidationException("New password must be at least 6 characters long");
        }
        
        // Check if new password is different from old password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ValidationException("New password must be different from current password");
        }
        
        // Validate password confirmation if provided
        if (request.getConfirmPassword() != null && 
            !request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("New password and confirmation do not match");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("Password changed successfully for user {}", user.getEmail());
    }
}