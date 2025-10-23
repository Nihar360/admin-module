package com.ecommerce.admin.controller;

import com.ecommerce.admin.dto.response.ApiResponse;
import com.ecommerce.admin.model.AdminActivityLog;
import com.ecommerce.admin.service.AdminActivityService;
import com.ecommerce.admin.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ReportController {
    
    private final ReportService reportService;
    private final AdminActivityService activityService;
    
    @GetMapping("/sales")
    public ResponseEntity<ApiResponse> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Generating sales report from {} to {}", startDate, endDate);
        var report = reportService.getSalesReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Sales report generated successfully", report));
    }
    
    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching top {} products", limit);
        var topProducts = reportService.getTopProducts(limit);
        return ResponseEntity.ok(ApiResponse.success("Top products retrieved successfully", topProducts));
    }
    
    @GetMapping("/activity-logs")
    public ResponseEntity<ApiResponse> getActivityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching activity logs with page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminActivityLog> activityLogs = activityService.getActivityLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success("Activity logs retrieved successfully", activityLogs));
    }
}
