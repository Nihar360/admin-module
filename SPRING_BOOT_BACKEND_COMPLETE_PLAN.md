# Spring Boot Admin Backend - Complete Implementation Plan

## Table of Contents
1. [Project Overview](#project-overview)
2. [Database Schema Changes](#database-schema-changes)
3. [Technology Stack](#technology-stack)
4. [Complete File Structure](#complete-file-structure)
5. [API Endpoints Reference](#api-endpoints-reference)
6. [Security Implementation](#security-implementation)
7. [Key Features Implementation](#key-features-implementation)
8. [Code Examples](#code-examples)
9. [Configuration Files](#configuration-files)
10. [Frontend Integration](#frontend-integration)
11. [Implementation Phases](#implementation-phases)
12. [Testing Strategy](#testing-strategy)

---

## Project Overview

This document outlines the complete plan for building a robust Spring Boot backend to support the e-commerce admin dashboard React frontend. The backend will provide secure RESTful APIs for all admin operations including order management, product management, user management, coupon management, reports, and analytics.

**Key Objectives:**
- Secure JWT-based authentication for admin users
- RESTful APIs for all admin dashboard features
- Proper database schema with new tables for missing functionality
- Role-based access control (ADMIN role only)
- Comprehensive error handling and validation
- Activity logging for audit trail
- Performance optimization with caching and pagination

---

## Database Schema Changes

### New Tables Required

#### 1. Coupons Table
```sql
CREATE TABLE coupons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    type ENUM('PERCENTAGE', 'FIXED') NOT NULL,
    value DECIMAL(10,2) NOT NULL,
    min_purchase DECIMAL(10,2) DEFAULT 0,
    max_discount DECIMAL(10,2),
    usage_limit INT NOT NULL,
    usage_count INT DEFAULT 0,
    expires_at DATETIME NOT NULL,
    is_active BIT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_code (code),
    INDEX idx_active_expires (is_active, expires_at)
);
```
**Purpose:** Store discount coupons for the CouponList page

#### 2. Notifications Table
```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type ENUM('NEW_ORDER', 'LOW_STOCK', 'ORDER_CANCELLED', 'REFUND_REQUEST') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    reference_id BIGINT,
    reference_type VARCHAR(50),
    is_read BIT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_created_at (created_at)
);
```
**Purpose:** Store admin notifications for the NotificationsPage

#### 3. Admin Activity Logs Table
```sql
CREATE TABLE admin_activity_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    action_type VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id VARCHAR(100),
    description TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES users(id),
    INDEX idx_admin_id (admin_id),
    INDEX idx_created_at (created_at),
    INDEX idx_action_type (action_type)
);
```
**Purpose:** Track all admin actions for audit trail in ReportsPage

#### 4. Order Refunds Table
```sql
CREATE TABLE order_refunds (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    refund_amount DECIMAL(10,2) NOT NULL,
    reason VARCHAR(500),
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED') NOT NULL,
    processed_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (processed_by) REFERENCES users(id),
    INDEX idx_order_id (order_id),
    INDEX idx_status (status)
);
```
**Purpose:** Track refund requests and processing in OrderDetails page

#### 5. Order Status History Table
```sql
CREATE TABLE order_status_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    old_status ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled'),
    new_status ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled') NOT NULL,
    changed_by BIGINT NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (changed_by) REFERENCES users(id),
    INDEX idx_order_id (order_id)
);
```
**Purpose:** Track order status changes for OrderTimeline component

#### 6. Product Reviews Table (Optional)
```sql
CREATE TABLE product_reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    review_text TEXT,
    is_verified_purchase BIT DEFAULT 0,
    is_approved BIT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_product_id (product_id),
    INDEX idx_user_id (user_id)
);
```
**Purpose:** Allow admins to moderate product reviews

### Modifications to Existing Tables

#### Update Users Table
```sql
ALTER TABLE users 
ADD COLUMN last_login TIMESTAMP NULL,
ADD COLUMN profile_image VARCHAR(500),
ADD COLUMN two_factor_enabled BIT DEFAULT 0;
```

#### Update Products Table
```sql
ALTER TABLE products
ADD COLUMN sku VARCHAR(100) UNIQUE,
ADD COLUMN meta_description VARCHAR(500),
ADD COLUMN meta_keywords VARCHAR(255);
```

### Migration Files Structure
```
src/main/resources/db/migration/
├── V1__add_coupon_table.sql
├── V2__add_notifications_table.sql
├── V3__add_admin_activity_logs.sql
├── V4__add_order_refunds.sql
├── V5__add_order_status_history.sql
├── V6__add_product_reviews.sql (optional)
└── V7__update_users_products.sql
```

---

## Technology Stack

### Core Framework
- **Spring Boot**: 3.2.x (Latest stable)
- **Java**: 17 or 21 LTS
- **Build Tool**: Maven or Gradle

### Dependencies (pom.xml)
```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Utilities -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    
    <!-- API Documentation -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.2.0</version>
    </dependency>
    
    <!-- Monitoring -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## Complete File Structure

```
ecommerce-admin-backend/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/admin/
│   │   │   ├── AdminApplication.java
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtConfig.java
│   │   │   │   ├── WebConfig.java
│   │   │   │   ├── SwaggerConfig.java
│   │   │   │   └── CacheConfig.java
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── CouponController.java
│   │   │   │   ├── NotificationController.java
│   │   │   │   ├── InventoryController.java
│   │   │   │   └── ReportController.java
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── DashboardService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── ProductService.java
│   │   │   │   ├── CategoryService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── CouponService.java
│   │   │   │   ├── NotificationService.java
│   │   │   │   ├── AdminActivityService.java
│   │   │   │   └── ReportService.java
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── OrderItemRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── AddressRepository.java
│   │   │   │   ├── CouponRepository.java
│   │   │   │   ├── NotificationRepository.java
│   │   │   │   ├── AdminActivityLogRepository.java
│   │   │   │   ├── OrderRefundRepository.java
│   │   │   │   ├── OrderStatusHistoryRepository.java
│   │   │   │   ├── ProductImageRepository.java
│   │   │   │   ├── ProductFeatureRepository.java
│   │   │   │   └── ProductSizeRepository.java
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Category.java
│   │   │   │   ├── Address.java
│   │   │   │   ├── Coupon.java
│   │   │   │   ├── Notification.java
│   │   │   │   ├── AdminActivityLog.java
│   │   │   │   ├── OrderRefund.java
│   │   │   │   ├── OrderStatusHistory.java
│   │   │   │   ├── ProductImage.java
│   │   │   │   ├── ProductFeature.java
│   │   │   │   ├── ProductSize.java
│   │   │   │   └── enums/
│   │   │   │       ├── OrderStatus.java
│   │   │   │       ├── PaymentMethod.java
│   │   │   │       ├── UserRole.java
│   │   │   │       ├── CouponType.java
│   │   │   │       ├── NotificationType.java
│   │   │   │       └── RefundStatus.java
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── PasswordChangeRequest.java
│   │   │   │   │   ├── ProductCreateRequest.java
│   │   │   │   │   ├── ProductUpdateRequest.java
│   │   │   │   │   ├── OrderStatusUpdateRequest.java
│   │   │   │   │   ├── CouponCreateRequest.java
│   │   │   │   │   ├── CouponUpdateRequest.java
│   │   │   │   │   ├── RefundRequest.java
│   │   │   │   │   └── StockAdjustmentRequest.java
│   │   │   │   │
│   │   │   │   └── response/
│   │   │   │       ├── LoginResponse.java
│   │   │   │       ├── DashboardStatsResponse.java
│   │   │   │       ├── SalesDataPoint.java
│   │   │   │       ├── OrderResponse.java
│   │   │   │       ├── OrderDetailResponse.java
│   │   │   │       ├── OrderTimelineResponse.java
│   │   │   │       ├── ProductResponse.java
│   │   │   │       ├── UserResponse.java
│   │   │   │       ├── CouponResponse.java
│   │   │   │       ├── NotificationResponse.java
│   │   │   │       ├── PageResponse.java
│   │   │   │       └── ApiResponse.java
│   │   │   │
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── SecurityUser.java
│   │   │   │
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── UnauthorizedException.java
│   │   │   │   ├── ValidationException.java
│   │   │   │   ├── BadRequestException.java
│   │   │   │   └── ErrorResponse.java
│   │   │   │
│   │   │   ├── aspect/
│   │   │   │   └── AdminActivityAspect.java
│   │   │   │
│   │   │   └── util/
│   │   │       ├── DateUtil.java
│   │   │       └── ValidationUtil.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__add_coupon_table.sql
│   │               ├── V2__add_notifications_table.sql
│   │               ├── V3__add_admin_activity_logs.sql
│   │               ├── V4__add_order_refunds.sql
│   │               ├── V5__add_order_status_history.sql
│   │               ├── V6__add_product_reviews.sql
│   │               └── V7__update_users_products.sql
│   │
│   └── test/
│       └── java/com/ecommerce/admin/
│           ├── controller/
│           │   ├── AuthControllerTest.java
│           │   ├── OrderControllerTest.java
│           │   └── ProductControllerTest.java
│           ├── service/
│           │   ├── OrderServiceTest.java
│           │   └── ProductServiceTest.java
│           └── repository/
│               └── OrderRepositoryTest.java
│
├── pom.xml
└── README.md
```

---

## API Endpoints Reference

### Base URL
```
http://localhost:8080/api/v1/admin
```

### 1. Authentication Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/auth/login` | Admin login | `{email, password}` | `{token, user}` |
| POST | `/auth/logout` | Admin logout | - | `{message}` |
| POST | `/auth/refresh` | Refresh JWT token | `{refreshToken}` | `{token}` |
| GET | `/auth/me` | Get current admin | - | `{user}` |
| PUT | `/auth/profile` | Update profile | `{fullName, mobile}` | `{user}` |
| PUT | `/auth/password` | Change password | `{oldPassword, newPassword}` | `{message}` |

### 2. Dashboard Endpoints

| Method | Endpoint | Description | Query Params | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/dashboard/stats` | Get dashboard statistics | `period` (optional) | `{totalRevenue, totalOrders, totalCustomers, changes}` |
| GET | `/dashboard/sales-data` | Get sales chart data | `days` (default: 30) | `[{date, revenue, orders}]` |
| GET | `/dashboard/recent-orders` | Get recent orders | `limit` (default: 10) | `[orders]` |
| GET | `/dashboard/low-stock` | Get low stock alerts | - | `[products]` |

### 3. Order Management Endpoints

| Method | Endpoint | Description | Query Params | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/orders` | List all orders | `status, search, page, size` | `{orders[], total, page}` |
| GET | `/orders/{id}` | Get order details | - | `{order, items, customer, address}` |
| PUT | `/orders/{id}/status` | Update order status | - | `{status}` in body |
| POST | `/orders/{id}/refund` | Process refund | - | `{amount, reason}` in body |
| GET | `/orders/{id}/timeline` | Get order timeline | - | `[{status, timestamp, changedBy}]` |
| GET | `/orders/export` | Export orders | `status, dateFrom, dateTo` | CSV/Excel file |

### 4. Product Management Endpoints

| Method | Endpoint | Description | Query Params | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/products` | List all products | `category, search, inStock, page, size` | `{products[], total}` |
| GET | `/products/{id}` | Get product details | - | `{product, images, features, sizes}` |
| POST | `/products` | Create new product | - | Product data in body |
| PUT | `/products/{id}` | Update product | - | Updated product data |
| DELETE | `/products/{id}` | Delete product | - | `{message}` |
| PUT | `/products/{id}/stock` | Update stock | - | `{quantity, operation}` in body |
| POST | `/products/{id}/images` | Upload images | - | Multipart file upload |
| GET | `/products/categories` | Get all categories | - | `[categories]` |

### 5. Category Management Endpoints

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/categories` | List all categories | `[{id, name, description, itemCount}]` |
| POST | `/categories` | Create category | `{name, description, image}` |
| PUT | `/categories/{id}` | Update category | Updated category data |
| DELETE | `/categories/{id}` | Delete category | `{message}` |

### 6. User Management Endpoints

| Method | Endpoint | Description | Query Params | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/users` | List all users | `search, status, page, size` | `{users[], total}` |
| GET | `/users/{id}` | Get user details | - | `{user, stats}` |
| PUT | `/users/{id}/status` | Block/Unblock user | - | `{active}` in body |
| GET | `/users/{id}/orders` | Get user orders | - | `[orders]` |
| GET | `/users/{id}/addresses` | Get user addresses | - | `[addresses]` |

### 7. Coupon Management Endpoints

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/coupons` | List all coupons | `[{id, code, type, value, usage}]` |
| GET | `/coupons/{id}` | Get coupon details | Coupon object |
| POST | `/coupons` | Create coupon | `{code, type, value, minPurchase, expiresAt}` |
| PUT | `/coupons/{id}` | Update coupon | Updated coupon data |
| DELETE | `/coupons/{id}` | Delete coupon | `{message}` |
| POST | `/coupons/validate` | Validate coupon | `{code, orderTotal}` |

### 8. Notification Endpoints

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/notifications` | Get all notifications | `[{id, type, title, message, isRead}]` |
| GET | `/notifications/unread` | Get unread count | `{count}` |
| PUT | `/notifications/{id}/read` | Mark as read | `{message}` |
| PUT | `/notifications/read-all` | Mark all as read | `{message}` |

### 9. Inventory Management Endpoints

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/inventory` | Get inventory status | `[{product, stock, status}]` |
| GET | `/inventory/low-stock` | Low stock products | `[products with stock < 10]` |
| GET | `/inventory/out-of-stock` | Out of stock products | `[products with stock = 0]` |
| PUT | `/inventory/{id}/adjust` | Adjust stock | `{quantity, type: 'add'/'remove'}` |

### 10. Report & Analytics Endpoints

| Method | Endpoint | Description | Query Params | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/reports/sales` | Sales report | `dateFrom, dateTo` | Sales data |
| GET | `/reports/revenue` | Revenue analytics | `period` | Revenue breakdown |
| GET | `/reports/top-products` | Top selling products | `limit` | `[products]` |
| GET | `/reports/customer-stats` | Customer statistics | - | Customer metrics |
| GET | `/reports/activity-logs` | Admin activity logs | `page, size` | `[logs]` |
| GET | `/reports/export` | Export report | `type, format` | File download |

---

## Security Implementation

### JWT Authentication Flow

```
1. Admin login with email/password
   ↓
2. Backend validates credentials
   ↓
3. Generate JWT token with claims (userId, role, email)
   ↓
4. Return token to frontend
   ↓
5. Frontend stores token (localStorage)
   ↓
6. Frontend sends token in Authorization header for all requests
   ↓
7. Backend validates token via JwtAuthenticationFilter
   ↓
8. Extract user from token and set in SecurityContext
   ↓
9. Check role-based permissions
   ↓
10. Execute request or return 401/403
```

### JWT Token Structure

```json
{
  "sub": "admin@example.com",
  "userId": 1,
  "role": "ADMIN",
  "iat": 1698765432,
  "exp": 1698851832
}
```

### SecurityConfig.java Key Points

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/admin/auth/login").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### CORS Configuration

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:5000",
                    "https://your-domain.replit.dev"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

---

## Key Features Implementation

### 1. Dashboard Statistics

```java
@Service
public class DashboardService {
    
    public DashboardStatsResponse getStats(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(period);
        
        // Current period stats
        BigDecimal revenue = orderRepository.getTotalRevenue(startDate, now);
        Long orders = orderRepository.countByDateRange(startDate, now);
        Long customers = userRepository.countByCreatedAtBetween(startDate, now);
        
        // Previous period for comparison
        LocalDateTime prevStart = startDate.minusDays(getDaysDifference(period));
        BigDecimal prevRevenue = orderRepository.getTotalRevenue(prevStart, startDate);
        Long prevOrders = orderRepository.countByDateRange(prevStart, startDate);
        
        // Calculate percentage changes
        double revenueChange = calculateChange(revenue, prevRevenue);
        double ordersChange = calculateChange(orders, prevOrders);
        
        return DashboardStatsResponse.builder()
            .totalRevenue(revenue)
            .totalOrders(orders)
            .totalCustomers(customers)
            .revenueChange(revenueChange)
            .ordersChange(ordersChange)
            .build();
    }
}
```

### 2. Order Status Update with History

```java
@Service
@Transactional
public class OrderService {
    
    public void updateOrderStatus(Long orderId, OrderStatus newStatus, Long adminId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        OrderStatus oldStatus = order.getStatus();
        
        // Update order
        order.setStatus(newStatus);
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredDate(LocalDateTime.now());
        }
        orderRepository.save(order);
        
        // Create status history entry
        OrderStatusHistory history = OrderStatusHistory.builder()
            .orderId(orderId)
            .oldStatus(oldStatus)
            .newStatus(newStatus)
            .changedBy(adminId)
            .build();
        statusHistoryRepository.save(history);
        
        // Log admin activity
        activityService.logActivity(adminId, "ORDER_STATUS_UPDATED", 
            "ORDER", orderId.toString(), 
            String.format("Status: %s → %s", oldStatus, newStatus));
        
        // Send notification to customer
        notificationService.notifyCustomer(order.getUserId(), 
            "Order status updated", 
            String.format("Your order #%s is now %s", 
                order.getOrderNumber(), newStatus));
    }
}
```

### 3. Coupon Validation

```java
@Service
public class CouponService {
    
    public CouponValidationResult validateCoupon(String code, BigDecimal orderTotal) {
        Coupon coupon = couponRepository.findByCode(code)
            .orElseThrow(() -> new ValidationException("Invalid coupon code"));
        
        // Check if active
        if (!coupon.getIsActive()) {
            throw new ValidationException("Coupon is not active");
        }
        
        // Check expiration
        if (coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Coupon has expired");
        }
        
        // Check usage limit
        if (coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new ValidationException("Coupon usage limit exceeded");
        }
        
        // Check minimum purchase
        if (orderTotal.compareTo(coupon.getMinPurchase()) < 0) {
            throw new ValidationException(
                String.format("Minimum purchase of $%.2f required", 
                    coupon.getMinPurchase())
            );
        }
        
        // Calculate discount
        BigDecimal discount = calculateDiscount(coupon, orderTotal);
        
        return CouponValidationResult.builder()
            .valid(true)
            .discount(discount)
            .coupon(coupon)
            .build();
    }
    
    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderTotal) {
        if (coupon.getType() == CouponType.PERCENTAGE) {
            BigDecimal discount = orderTotal
                .multiply(BigDecimal.valueOf(coupon.getValue()))
                .divide(BigDecimal.valueOf(100));
            
            if (coupon.getMaxDiscount() != null) {
                return discount.min(coupon.getMaxDiscount());
            }
            return discount;
        } else {
            return BigDecimal.valueOf(coupon.getValue());
        }
    }
}
```

### 4. Low Stock Notifications

```java
@Service
public class NotificationService {
    
    @Scheduled(cron = "0 0 9 * * *") // Daily at 9 AM
    public void checkLowStock() {
        List<Product> lowStockProducts = productRepository
            .findByStockQuantityLessThan(10);
        
        if (!lowStockProducts.isEmpty()) {
            List<User> admins = userRepository.findByRole(UserRole.ADMIN);
            
            for (User admin : admins) {
                Notification notification = Notification.builder()
                    .userId(admin.getId())
                    .type(NotificationType.LOW_STOCK)
                    .title("Low Stock Alert")
                    .message(String.format("%d products are low on stock", 
                        lowStockProducts.size()))
                    .build();
                
                notificationRepository.save(notification);
            }
        }
    }
}
```

### 5. Admin Activity Logging with AOP

```java
@Aspect
@Component
@Slf4j
public class AdminActivityAspect {
    
    @Autowired
    private AdminActivityService activityService;
    
    @AfterReturning(
        pointcut = "@annotation(logActivity)",
        returning = "result"
    )
    public void logActivity(JoinPoint joinPoint, LogActivity logActivity, Object result) {
        SecurityUser user = (SecurityUser) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
        
        HttpServletRequest request = 
            ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes())
                .getRequest();
        
        activityService.logActivity(
            user.getId(),
            logActivity.action(),
            logActivity.resourceType(),
            extractResourceId(joinPoint),
            logActivity.description(),
            request.getRemoteAddr(),
            request.getHeader("User-Agent")
        );
    }
}

// Custom annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogActivity {
    String action();
    String resourceType();
    String description() default "";
}

// Usage
@LogActivity(
    action = "PRODUCT_CREATED",
    resourceType = "PRODUCT",
    description = "New product created"
)
public ProductResponse createProduct(ProductCreateRequest request) {
    // ...
}
```

---

## Code Examples

### Entity Example: Order.java

```java
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private Address shippingAddress;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal shipping;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discount;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "coupon_code")
    private String couponCode;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;
    
    @Column(length = 1000)
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### Repository Example: OrderRepository.java

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:search IS NULL OR " +
           "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.user.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Order> findByFilters(
        @Param("status") OrderStatus status,
        @Param("search") String search,
        Pageable pageable
    );
    
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "AND o.status != 'CANCELLED'")
    BigDecimal getTotalRevenue(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT new com.ecommerce.admin.dto.response.SalesDataPoint(" +
           "CAST(o.createdAt AS date), " +
           "COALESCE(SUM(o.total), 0), " +
           "COUNT(o)) " +
           "FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(o.createdAt AS date) " +
           "ORDER BY CAST(o.createdAt AS date)")
    List<SalesDataPoint> getSalesData(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
```

### Controller Example: OrderController.java

```java
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Fetching orders - status: {}, search: {}, page: {}", 
            status, search, page);
        
        PageResponse<OrderResponse> response = orderService.getOrders(
            status, search, page, size);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderDetails(
            @PathVariable Long id) {
        
        OrderDetailResponse response = orderService.getOrderDetails(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/status")
    @LogActivity(
        action = "ORDER_STATUS_UPDATED",
        resourceType = "ORDER"
    )
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        
        SecurityUser user = (SecurityUser) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
        
        orderService.updateOrderStatus(id, request.getStatus(), user.getId());
        
        return ResponseEntity.ok(
            ApiResponse.success("Order status updated successfully"));
    }
    
    @PostMapping("/{id}/refund")
    @LogActivity(
        action = "REFUND_PROCESSED",
        resourceType = "ORDER"
    )
    public ResponseEntity<ApiResponse> processRefund(
            @PathVariable Long id,
            @Valid @RequestBody RefundRequest request) {
        
        SecurityUser user = (SecurityUser) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
        
        orderService.processRefund(id, request, user.getId());
        
        return ResponseEntity.ok(
            ApiResponse.success("Refund processed successfully"));
    }
    
    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<OrderTimelineResponse>> getOrderTimeline(
            @PathVariable Long id) {
        
        List<OrderTimelineResponse> timeline = 
            orderService.getOrderTimeline(id);
        
        return ResponseEntity.ok(timeline);
    }
}
```

### DTO Example: DashboardStatsResponse.java

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalCustomers;
    private BigDecimal averageOrderValue;
    
    private Double revenueChange;   // Percentage change from previous period
    private Double ordersChange;
    private Double customersChange;
    
    private List<SalesDataPoint> salesData;
}

@Data
@AllArgsConstructor
public class SalesDataPoint {
    private LocalDate date;
    private BigDecimal revenue;
    private Long orders;
}
```

### Exception Handler Example

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        
        log.error("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {
        
        log.error("Validation error: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Invalid input data")
            .path(request.getRequestURI())
            .validationErrors(errors)
            .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unexpected error", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .path(request.getRequestURI())
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }
}
```

---

## Configuration Files

### application.yml

```yaml
spring:
  application:
    name: ecommerce-admin-backend
  
  profiles:
    active: dev
  
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&serverTimezone=UTC
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
  
  security:
    jwt:
      secret: ${JWT_SECRET:your-super-secret-key-change-this-in-production}
      expiration: 86400000  # 24 hours in milliseconds
      refresh-expiration: 604800000  # 7 days

server:
  port: 8080
  servlet:
    context-path: /api/v1
  error:
    include-message: always
    include-binding-errors: always

logging:
  level:
    com.ecommerce.admin: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  file:
    name: logs/application.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
```

### application-dev.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce_dev?useSSL=false
  
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update

logging:
  level:
    com.ecommerce.admin: DEBUG
```

### application-prod.yml

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate

logging:
  level:
    com.ecommerce.admin: WARN
```

---

## Frontend Integration

### Update adminApi.ts

Replace mock functions with real API calls:

```javascript
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

// Create axios instance
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add JWT token to requests
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('adminToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle token expiration
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('adminToken');
      window.location.href = '/admin/login';
    }
    return Promise.reject(error);
  }
);

export const adminApi = {
  
  // Authentication
  login: async (email: string, password: string) => {
    const response = await apiClient.post('/admin/auth/login', {
      email, password
    });
    localStorage.setItem('adminToken', response.data.token);
    return response.data;
  },
  
  logout: async () => {
    await apiClient.post('/admin/auth/logout');
    localStorage.removeItem('adminToken');
  },
  
  // Dashboard
  getDashboardStats: async () => {
    const response = await apiClient.get('/admin/dashboard/stats');
    return response.data;
  },
  
  getSalesData: async (days: number = 30) => {
    const response = await apiClient.get('/admin/dashboard/sales-data', {
      params: { days }
    });
    return response.data;
  },
  
  // Orders
  getOrders: async (filters?: {
    status?: string;
    search?: string;
    page?: number;
    limit?: number;
  }) => {
    const response = await apiClient.get('/admin/orders', {
      params: {
        status: filters?.status,
        search: filters?.search,
        page: filters?.page || 0,
        size: filters?.limit || 20
      }
    });
    return response.data;
  },
  
  getOrder: async (id: string) => {
    const response = await apiClient.get(`/admin/orders/${id}`);
    return response.data;
  },
  
  updateOrderStatus: async (id: string, status: string) => {
    const response = await apiClient.put(`/admin/orders/${id}/status`, {
      status
    });
    return response.data;
  },
  
  // Products
  getProducts: async (filters?: {
    category?: string;
    search?: string;
    inStock?: boolean;
  }) => {
    const response = await apiClient.get('/admin/products', {
      params: filters
    });
    return response.data.products;
  },
  
  getProduct: async (id: string) => {
    const response = await apiClient.get(`/admin/products/${id}`);
    return response.data;
  },
  
  createProduct: async (data: any) => {
    const response = await apiClient.post('/admin/products', data);
    return response.data;
  },
  
  updateProduct: async (id: string, data: any) => {
    const response = await apiClient.put(`/admin/products/${id}`, data);
    return response.data;
  },
  
  deleteProduct: async (id: string) => {
    await apiClient.delete(`/admin/products/${id}`);
  },
  
  // Coupons
  getCoupons: async () => {
    const response = await apiClient.get('/admin/coupons');
    return response.data;
  },
  
  createCoupon: async (data: any) => {
    const response = await apiClient.post('/admin/coupons', data);
    return response.data;
  },
  
  updateCoupon: async (id: string, data: any) => {
    const response = await apiClient.put(`/admin/coupons/${id}`, data);
    return response.data;
  },
  
  deleteCoupon: async (id: string) => {
    await apiClient.delete(`/admin/coupons/${id}`);
  },
  
  // Users
  getUsers: async (filters?: { search?: string; status?: string }) => {
    const response = await apiClient.get('/admin/users', {
      params: filters
    });
    return response.data.users;
  },
  
  getUser: async (id: string) => {
    const response = await apiClient.get(`/admin/users/${id}`);
    return response.data;
  }
};
```

### Environment Variables (.env)

```env
VITE_API_URL=http://localhost:8080/api/v1
```

For production deployment on Replit:
```env
VITE_API_URL=https://your-backend.replit.app/api/v1
```

---

## Implementation Phases

### Phase 1: Core Setup (Week 1)
**Goal:** Get basic project structure and authentication working

**Tasks:**
1. Create Spring Boot project with Maven/Gradle
2. Add all dependencies to pom.xml
3. Set up database connection
4. Run migration scripts (V1-V7.sql)
5. Create all entity classes and enums
6. Create all repository interfaces
7. Implement JWT security (JwtTokenProvider, SecurityConfig)
8. Implement AuthService and AuthController
9. Test login endpoint with Postman
10. Configure CORS for React frontend

**Deliverable:** Working admin login with JWT token generation

### Phase 2: Dashboard & Orders (Week 2)
**Goal:** Implement dashboard statistics and order management

**Tasks:**
1. Implement DashboardService (stats calculation, sales data)
2. Implement DashboardController
3. Test dashboard endpoints
4. Implement OrderService (list, details, status update, refund)
5. Implement OrderStatusHistoryService for timeline
6. Implement OrderController
7. Create AdminActivityAspect for logging
8. Test all order endpoints
9. Integrate frontend dashboard with backend

**Deliverable:** Working dashboard and order management

### Phase 3: Products & Inventory (Week 3)
**Goal:** Complete product and inventory management

**Tasks:**
1. Implement ProductService (CRUD, search, filters)
2. Implement CategoryService
3. Implement InventoryController
4. Implement ProductController
5. Handle product images, features, sizes
6. Implement stock management
7. Test all product endpoints
8. Integrate frontend product pages

**Deliverable:** Full product and inventory management

### Phase 4: Additional Features (Week 4)
**Goal:** Complete remaining admin features

**Tasks:**
1. Implement CouponService and CouponController
2. Implement NotificationService
3. Implement scheduled tasks (low stock alerts)
4. Implement UserService and UserController
5. Implement ReportService for analytics
6. Test all endpoints
7. Integrate all frontend pages

**Deliverable:** Complete admin backend functionality

### Phase 5: Testing & Deployment (Week 5)
**Goal:** Test, optimize, and deploy

**Tasks:**
1. Write unit tests for services
2. Write integration tests for controllers
3. Performance testing and optimization
4. Add caching where appropriate
5. Security audit
6. Complete Swagger documentation
7. Deploy backend (Replit/Railway/Heroku)
8. End-to-end testing with frontend
9. Fix any bugs
10. Documentation and handoff

**Deliverable:** Production-ready backend

---

## Testing Strategy

### Unit Tests Example

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private AdminActivityService activityService;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    void updateOrderStatus_ShouldUpdateSuccessfully() {
        // Given
        Long orderId = 1L;
        Order order = Order.builder()
            .id(orderId)
            .status(OrderStatus.PENDING)
            .build();
        
        when(orderRepository.findById(orderId))
            .thenReturn(Optional.of(order));
        
        // When
        orderService.updateOrderStatus(orderId, OrderStatus.PROCESSING, 1L);
        
        // Then
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        verify(orderRepository).save(order);
        verify(activityService).logActivity(any(), any(), any(), any(), any());
    }
    
    @Test
    void updateOrderStatus_ShouldThrowException_WhenOrderNotFound() {
        // Given
        when(orderRepository.findById(anyLong()))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
            orderService.updateOrderStatus(999L, OrderStatus.PROCESSING, 1L)
        );
    }
}
```

### Integration Test Example

```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrders_ShouldReturnOrderList() throws Exception {
        mockMvc.perform(get("/api/v1/admin/orders")
                .param("status", "pending")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orders").isArray())
            .andExpect(jsonPath("$.total").isNumber());
    }
}
```

---

## Performance Considerations

### 1. Database Indexing
Ensure these indexes exist:
- `orders(status, created_at)`
- `products(category_id, in_stock)`
- `users(role, active)`
- `notifications(user_id, is_read)`
- `admin_activity_logs(admin_id, created_at)`

### 2. Query Optimization
- Use pagination for all list endpoints
- Use `@EntityGraph` to avoid N+1 queries
- Use database-level aggregations for statistics

### 3. Caching Strategy
```java
@Cacheable(value = "dashboard-stats", key = "'stats-' + #period")
public DashboardStatsResponse getStats(String period) {
    // Expensive calculation
}

@CacheEvict(value = "products", allEntries = true)
public void updateProduct(Long id, ProductUpdateRequest request) {
    // Update product
}
```

---

## Summary Checklist

- [ ] 6 new database tables created
- [ ] 2 existing tables modified
- [ ] JWT authentication implemented
- [ ] 10 controller classes created
- [ ] 10+ service classes implemented
- [ ] All repository interfaces created
- [ ] Global exception handling
- [ ] CORS configuration for React
- [ ] Swagger documentation
- [ ] Admin activity logging with AOP
- [ ] Unit tests (80%+ coverage)
- [ ] Integration tests for critical paths
- [ ] Frontend integration complete
- [ ] Production deployment

---

**Estimated Development Time:** 4-5 weeks
**Team Size:** 2-3 developers
**Lines of Code:** ~15,000-20,000 (including tests)

This document serves as your complete reference for building the Spring Boot admin backend. Follow the phases sequentially and refer to the code examples for implementation details.
