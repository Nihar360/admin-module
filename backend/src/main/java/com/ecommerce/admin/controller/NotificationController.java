package com.ecommerce.admin.controller;

import com.ecommerce.admin.dto.response.ApiResponse;
import com.ecommerce.admin.dto.response.NotificationResponse;
import com.ecommerce.admin.security.SecurityUser;
import com.ecommerce.admin.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
@Slf4j
@Validated
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getNotifications(@AuthenticationPrincipal SecurityUser currentUser) {
        log.info("Fetching all notifications for admin: {}", currentUser.getId());
        List<NotificationResponse> notifications = notificationService.getAllNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
    }
    
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse> getUnreadCount(@AuthenticationPrincipal SecurityUser currentUser) {
        log.info("Fetching unread notification count for admin: {}", currentUser.getId());
        Long count = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved successfully", count));
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Long id) {
        log.info("Marking notification {} as read", id);
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse> markAllAsRead(@AuthenticationPrincipal SecurityUser currentUser) {
        log.info("Marking all notifications as read for admin: {}", currentUser.getId());
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }
}
