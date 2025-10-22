package com.ecommerce.admin.controller;

import com.ecommerce.admin.dto.request.LoginRequest;
import com.ecommerce.admin.dto.request.PasswordChangeRequest;
import com.ecommerce.admin.dto.response.ApiResponse;
import com.ecommerce.admin.dto.response.LoginResponse;
import com.ecommerce.admin.dto.response.UserResponse;
import com.ecommerce.admin.security.SecurityUser;
import com.ecommerce.admin.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Admin login attempt for email: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(@AuthenticationPrincipal SecurityUser currentUser) {
        log.info("Fetching current user details for admin: {}", currentUser.getId());
        UserResponse response = authService.getCurrentUser(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("User details retrieved", response));
    }
    
    @PutMapping("/password")
    public ResponseEntity<ApiResponse> changePassword(
            @AuthenticationPrincipal SecurityUser currentUser,
            @Valid @RequestBody PasswordChangeRequest request) {
        log.info("Password change request for admin: {}", currentUser.getId());
        authService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }
}
