package com.ecommerce.admin.service;

import com.ecommerce.admin.model.AdminActivityLog;
import com.ecommerce.admin.repository.AdminActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminActivityService {
    
    private final AdminActivityLogRepository activityLogRepository;
    
    @Transactional
    public void logActivity(Long adminId, String actionType, String resourceType, 
                           String resourceId, String description) {
        logActivity(adminId, actionType, resourceType, resourceId, description, null, null);
    }
    
    @Transactional
    public void logActivity(Long adminId, String actionType, String resourceType, 
                           String resourceId, String description, 
                           String ipAddress, String userAgent) {
        AdminActivityLog log = AdminActivityLog.builder()
                .adminId(adminId)
                .actionType(actionType)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .description(description)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
        
        activityLogRepository.save(log);
        this.log.debug("Activity logged: {} - {} - {}", adminId, actionType, resourceType);
    }
    
    @Transactional(readOnly = true)
    public Page<AdminActivityLog> getActivityLogs(Pageable pageable) {
        return activityLogRepository.findByOrderByCreatedAtDesc(pageable);
    }
}
