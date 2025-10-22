package com.ecommerce.admin.aspect;

import com.ecommerce.admin.service.AdminActivityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminActivityAspect {

    private final AdminActivityService activityService;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface LogActivity {
        String action();
        String resourceType();
    }

    @Around("@annotation(logActivity)")
    public Object logAdminActivity(ProceedingJoinPoint joinPoint, LogActivity logActivity) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String action = logActivity.action();
        String resourceType = logActivity.resourceType();
        
        HttpServletRequest request = getCurrentHttpRequest();
        String ipAddress = getClientIpAddress(request);
        String userAgent = getUserAgent(request);
        
        Long adminId = getAdminId(authentication);
        String resourceId = extractResourceId(joinPoint);
        String description = generateDescription(action, resourceType, joinPoint);

        long startTime = System.currentTimeMillis();
        Object result = null;
        boolean success = true;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            if (adminId != null) {
                try {
                    activityService.logActivity(
                        adminId,
                        action,
                        resourceType,
                        resourceId,
                        description,
                        ipAddress,
                        userAgent
                    );
                    
                    log.info("Admin activity logged - Admin: {}, Action: {}, Resource: {}, Duration: {}ms, Success: {}", 
                        adminId, action, resourceType, duration, success);
                } catch (Exception e) {
                    log.error("Failed to log admin activity", e);
                }
            }
        }
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip != null ? ip : "unknown";
    }

    private String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "unknown";
    }

    private Long getAdminId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            try {
                return Long.parseLong(username);
            } catch (NumberFormatException e) {
                log.warn("Could not parse admin ID from username: {}", username);
            }
        }
        
        return null;
    }

    private String extractResourceId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            Object firstArg = args[0];
            if (firstArg instanceof Long) {
                return firstArg.toString();
            } else if (firstArg instanceof String) {
                return (String) firstArg;
            }
        }
        return null;
    }

    private String generateDescription(String action, String resourceType, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        Object[] args = joinPoint.getArgs();
        
        StringBuilder description = new StringBuilder();
        description.append(action).append(" ").append(resourceType);
        
        if (args.length > 0) {
            description.append(" - Method: ").append(methodName);
        }
        
        return description.toString();
    }
}
