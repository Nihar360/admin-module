# Admin Module - Dynamic Database Integration Plan

## Project Status
✅ **Import Complete** - All workflows running successfully  
📅 **Planning Date**: October 24, 2025

---

## Executive Summary

This document outlines the comprehensive plan to make all Admin Module features 100% dynamic with real-time database integration. The backend infrastructure is already robust with Spring Boot controllers, services, and repositories in place. The primary work involves:

1. **Frontend Integration** - Connect React components to existing backend APIs
2. **Real-time Notifications** - Implement WebSocket/SSE for live order notifications
3. **Data Validation** - Ensure all hardcoded values are replaced with database queries
4. **Settings Persistence** - Store admin preferences in database

---

## Current State Analysis

### ✅ Backend Strengths
- **Complete API Coverage**: All major controllers implemented (Dashboard, Orders, Products, Categories, Users, Coupons, Inventory, Reports, Notifications)
- **Service Layer**: Business logic properly separated
- **Security**: JWT authentication, role-based access control
- **Database Schema**: Comprehensive schema with proper relationships

### ⚠️ Frontend Gaps
- Hardcoded data in several components
- Limited API integration in some hooks
- Missing real-time notification subscriptions
- Category dropdowns using static values
- Stock management needs direct API calls

---

## Module-by-Module Implementation Plan

### 1. **Dashboard** 📊

#### Current State
- Backend: `DashboardController` with `/stats` endpoint exists
- Frontend: `useAdminDashboard` hook may have mock data

#### Required Changes

**Backend** (✅ Already Done):
```
GET /admin/dashboard/stats?days=30
```
Returns: totalOrders, totalCustomers, ordersChange, customersChange, recentOrders

**Frontend** (🔧 To Implement):
1. Update `hooks/useAdminDashboard.ts`:
   - Call `adminApi.getDashboardStats()`
   - Remove any hardcoded values
   - Handle loading/error states

2. Update `src/pages/AdminDashboard.tsx`:
   - Display real `totalOrders` and `totalCustomers`
   - Show dynamic `recentOrders` list
   - Ensure stats refresh on mount

**Verification**:
- Check dashboard displays live order count
- Verify customer count updates after new user registration
- Confirm recent orders show latest 5-10 entries

---

### 2. **Notifications** 🔔

#### Current State
- Backend: `NotificationController` with CRUD endpoints
- Frontend: Basic notifications page exists
- Missing: Real-time push notifications

#### Required Implementation

**Backend** (🆕 To Add - **CRITICAL: Not Yet Implemented**):
1. **Create NotificationEvent System** (NEW REQUIREMENT):
   
   Create `OrderCreatedEvent.java`:
   ```java
   @Getter
   @AllArgsConstructor
   public class OrderCreatedEvent extends ApplicationEvent {
       private final Long orderId;
       private final String orderNumber;
       private final Long userId;
       
       public OrderCreatedEvent(Object source, Long orderId, String orderNumber, Long userId) {
           super(source);
           this.orderId = orderId;
           this.orderNumber = orderNumber;
           this.userId = userId;
       }
   }
   ```
   
   Update `OrderService.java` to publish event:
   ```java
   @Service
   public class OrderService {
       @Autowired private ApplicationEventPublisher eventPublisher;
       
       public Order createOrder(...) {
           // ... save order logic ...
           
           // Publish event for notifications
           eventPublisher.publishEvent(new OrderCreatedEvent(
               this, order.getId(), order.getOrderNumber(), order.getUserId()
           ));
           
           return order;
       }
   }
   ```
   
   Create `NotificationEventListener.java`:
   ```java
   @Component
   @RequiredArgsConstructor
   public class NotificationEventListener {
       private final NotificationService notificationService;
       private final UserRepository userRepository;
       
       @EventListener
       @Async
       public void onOrderCreated(OrderCreatedEvent event) {
           // Find all admin users
           List<User> admins = userRepository.findByRole(UserRole.ADMIN);
           
           // Create notification for each admin
           for (User admin : admins) {
               notificationService.createNotification(
                   admin.getId(),
                   "New Order",
                   "Order #" + event.getOrderNumber() + " has been placed",
                   NotificationType.NEW_ORDER,
                   event.getOrderId()
               );
           }
       }
   }
   ```

2. **Add SSE/WebSocket Endpoint** (Optional for Phase 2):
   ```java
   @GetMapping(path = "/admin/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
   public Flux<NotificationResponse> streamNotifications(@AuthenticationPrincipal SecurityUser user)
   ```

**Frontend** (🔧 To Implement):
1. Create `NotificationContext.tsx`:
   - Poll `/admin/notifications/unread` every 30 seconds
   - Store notifications in context
   - Provide `unreadCount` to header

2. Update `AdminLayout.tsx`:
   - Display notification bell with badge count
   - Click opens notifications dropdown
   - Mark as read functionality

3. Update `OrderService.java`:
   - After order creation, trigger notification event
   - Send to all admin users

**Verification**:
- Create test order, verify notification appears in header
- Check notification count updates immediately
- Confirm "Mark All Read" clears badge

---

### 3. **Orders** 📦

#### Current State
- Backend: Full CRUD with status updates, refunds, timeline
- Frontend: OrderList and OrderDetails pages exist

#### Required Changes

**Backend** (✅ Already Done):
```
GET /admin/orders?status={status}&search={search}&page={page}&size={size}
GET /admin/orders/{id}
PUT /admin/orders/{id}/status
POST /admin/orders/{id}/refund
GET /admin/orders/{id}/timeline
```

**Frontend** (🔧 To Implement):
1. Update `hooks/useAdminOrders.ts`:
   - Implement filter functionality
   - Call `adminApi.getOrders(filters)`
   - Handle pagination

2. Update `OrderList.tsx`:
   - Add "Take Action" dropdown with:
     - View Details
     - Update Status (Pending → Processing → Shipped → Delivered)
     - Process Refund
     - Cancel Order
   - Implement search by order number or customer
   - Filter by status

3. Update `OrderDetails.tsx`:
   - Load full order with items
   - Show order timeline from `/admin/orders/{id}/timeline`
   - Status update modal
   - Refund modal

**Verification**:
- Search orders by number
- Filter by "Pending" status
- Update order status → verify database changes
- Process refund → check refund table entry

---

### 4. **Products** 🛍️

#### Current State
- Backend: Complete CRUD + stock adjustment
- Frontend: Product list and form exist
- Issue: Categories are hardcoded

#### Required Changes

**Backend** (✅ Already Done):
```
GET /admin/categories - Returns all categories
GET /admin/products?categoryId={id}&search={search}
POST /admin/products
PUT /admin/products/{id}
DELETE /admin/products/{id}
GET /admin/products/{id}
```

**Frontend** (🔧 To Implement):
1. Update `ProductForm.tsx`:
   - **Remove hardcoded categories array**
   - Fetch categories: `const { data: categories } = useQuery('categories', adminApi.getCategories)`
   - Populate category dropdown dynamically
   - On edit: Load product data via `adminApi.getProduct(id)`
   - Show category name (not just ID)

2. Update `ProductList.tsx`:
   - Display `product.categoryName` instead of hardcoded value
   - Two-step delete confirmation:
     ```tsx
     <ConfirmDialog
       title="Delete Product?"
       description="Are you sure? This action cannot be undone."
       onConfirm={() => setShowSecondConfirm(true)}
     />
     ```

3. Create `hooks/useCategories.ts`:
   ```tsx
   export const useCategories = () => {
     return useQuery('categories', () => adminApi.getCategories());
   };
   ```

**Verification**:
- Check category dropdown loads from database
- Edit product → verify data pre-fills correctly
- Delete product → confirm two-step dialog
- Verify category name displays in product list

---

### 5. **Inventory** 📦

#### Current State
- Backend: Uses ProductService for inventory data
- Frontend: Inventory page exists

#### Required Changes

**Backend** (✅ Already Done):
```
GET /admin/inventory?categoryId={id}&search={search}
GET /admin/inventory/low-stock
PUT /admin/products/{id}/stock - Adjust stock
```

**Frontend** (🔧 To Implement):
1. Update `InventoryManagement.tsx`:
   - **Remove "In Stock" label**
   - Show actual stock number: `{product.stockCount} units`
   - Display category name from database
   - Add/Remove stock via API:
     ```tsx
     await adminApi.adjustStock(productId, {
       type: 'add', // or 'remove'
       quantity: amount
     });
     ```

2. Stock Actions Column:
   - **Increase Stock** button → Opens dialog
   - **Decrease Stock** button → Opens dialog
   - **Set Stock** → Manual entry
   - Update triggers immediate refresh

**Verification**:
- Check stock displays actual number (not "In Stock" badge)
- Increase stock by 10 → verify database update
- Decrease stock by 5 → confirm stock count changes
- Verify category displays correctly

---

### 6. **Coupons** 🎟️

#### Current State
- Backend: Full CRUD operations exist
- Frontend: Coupon list exists

#### Required Changes

**Backend** (✅ Already Done):
```
GET /admin/coupons
POST /admin/coupons
PUT /admin/coupons/{id}
DELETE /admin/coupons/{id}
```

**🆕 Additional Backend**:
1. **Coupon Application Logic** (in OrderService):
   ```java
   public BigDecimal applyCoupon(String couponCode, BigDecimal subtotal) {
       Coupon coupon = couponRepository.findByCode(couponCode)
           .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
       
       // Validate: is active, not expired, usage limit
       if (!coupon.getIsActive() || coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
           throw new BadRequestException("Coupon is invalid or expired");
       }
       
       // Apply discount
       BigDecimal discount = coupon.getType() == CouponType.PERCENTAGE
           ? subtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100))
           : coupon.getValue();
       
       // Update usage count
       coupon.setUsageCount(coupon.getUsageCount() + 1);
       couponRepository.save(coupon);
       
       return discount;
   }
   ```

**Frontend** (🔧 To Implement):
1. Update `CouponList.tsx`:
   - Fetch coupons from `adminApi.getCoupons()`
   - Add Coupon form with validation:
     - Code (unique, uppercase)
     - Type (percentage/fixed)
     - Value
     - Min Purchase
     - Usage Limit
     - Expiry Date
   - Display all columns: Code, Type, Discount, Usage, Expires, Status, Actions

2. Coupon Application:
   - During checkout, apply coupon code
   - Validate via backend
   - Display discount in order total

**Verification**:
- Create coupon "SAVE20" with 20% discount
- Apply to order → verify discount calculation
- Check usage count increments
- Verify expired coupons are rejected

---

### 7. **Users** 👥

#### Current State
- Backend: User CRUD and status updates exist
- Frontend: User list page
- Issue: Actions redirect to login

#### Required Changes

**Backend** (✅ Already Done):
```
GET /admin/users?search={search}&isActive={true/false}
GET /admin/users/{id}
PUT /admin/users/{id}/status
GET /admin/users/{id}/orders
```

**Frontend** (🔧 To Implement):
1. **Fix Redirect Issue**:
   - Check `AdminAuthGuard.tsx` - ensure token is valid
   - Verify API client includes auth header:
     ```tsx
     axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
     ```

2. Update `UserList.tsx`:
   - Fetch: `adminApi.getUsers({ search, isActive })`
   - Display columns:
     - Customer (name + email)
     - Phone
     - Orders (count)
     - Total Spent
     - Joined (date)
     - Status (Active/Inactive)
     - Actions

3. User Actions Menu:
   - **View Details** → Navigate to user detail page
   - **Activate/Deactivate** → Call `adminApi.updateUserStatus(id, isActive)`
   - **View Orders** → Show user's order history
   - **Ban User** (optional)

4. Create `UserDetails.tsx` page:
   - Show user profile
   - Display order history
   - Activity timeline

**Verification**:
- Click user actions → should NOT redirect to login
- Activate/deactivate user → check database update
- View user details → confirm data loads
- Check order history displays correctly

---

### 8. **Reports** 📈

#### Current State
- Backend: Sales report endpoint exists
- Frontend: Reports page has hardcoded data

#### Required Changes

**Backend** (🆕 To Add):
1. **Enhance ReportService**:
   ```java
   public SalesReportDTO getSalesReport(LocalDateTime start, LocalDateTime end) {
       // Total Revenue
       BigDecimal revenue = orderRepository.sumTotalBetweenDates(start, end);
       
       // Total Orders
       Long orderCount = orderRepository.countBetweenDates(start, end);
       
       // Average Order Value
       BigDecimal avgOrderValue = revenue.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);
       
       // Sales by Category
       List<CategorySalesDTO> categorySales = orderRepository.getSalesByCategory(start, end);
       
       return new SalesReportDTO(revenue, orderCount, avgOrderValue, categorySales);
   }
   ```

2. **Add Repository Methods**:
   ```java
   @Query("SELECT SUM(o.total) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
   BigDecimal sumTotalBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
   
   @Query("SELECT new com.ecommerce.admin.dto.CategorySalesDTO(c.name, COUNT(oi), SUM(oi.total)) " +
          "FROM OrderItem oi JOIN oi.product p JOIN p.category c " +
          "JOIN oi.order o WHERE o.createdAt BETWEEN :start AND :end " +
          "GROUP BY c.name ORDER BY SUM(oi.total) DESC")
   List<CategorySalesDTO> getSalesByCategory(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
   ```

**Frontend** (🔧 To Implement):
1. Update `ReportsPage.tsx`:
   - **Remove all hardcoded numbers**
   - Fetch: `const { data: report } = useQuery(['sales-report', dateRange], () => adminApi.getSalesReport(dateRange))`
   - Display:
     - Total Revenue: `${report.totalRevenue.toFixed(2)}`
     - Total Orders: `{report.totalOrders}`
     - Average Order Value: `${report.avgOrderValue.toFixed(2)}`
   - Sales by Category Table:
     - Category | Orders | Revenue
     - Map from `report.categorySales`

2. Add Date Range Picker:
   - Last 7 days, 30 days, 90 days, custom range
   - Update report on date change

**Verification**:
- Check revenue matches sum of all orders in period
- Verify order count is accurate
- Calculate AOV manually → compare with report
- Confirm sales by category sums correctly

---

### 9. **Settings** ⚙️

#### Current State
- Frontend: Settings page with tabs (Profile, Notifications, Security)
- Missing: Backend persistence

#### Required Changes

**Backend** (🆕 To Add):
1. **Create SettingsController**:
   ```java
   @PutMapping("/admin/settings/profile")
   public ResponseEntity<ApiResponse> updateProfile(
       @AuthenticationPrincipal SecurityUser currentUser,
       @Valid @RequestBody ProfileUpdateRequest request) {
       userService.updateProfile(currentUser.getId(), request);
       return ResponseEntity.ok(ApiResponse.success("Profile updated"));
   }
   
   @PutMapping("/admin/settings/notifications")
   public ResponseEntity<ApiResponse> updateNotificationPreferences(
       @AuthenticationPrincipal SecurityUser currentUser,
       @Valid @RequestBody NotificationPreferencesRequest request) {
       userService.updateNotificationPreferences(currentUser.getId(), request);
       return ResponseEntity.ok(ApiResponse.success("Preferences updated"));
   }
   ```

2. **Add to User Model** (if not exists):
   ```java
   private Boolean emailNotifications = true;
   private Boolean smsNotifications = false;
   private Boolean twoFactorEnabled = false;
   ```

**Frontend** (🔧 To Implement):
1. Update `SettingsPage.tsx`:
   - Profile tab:
     ```tsx
     const handleSaveProfile = async (formData) => {
       await adminApi.updateProfile(formData);
       toast('Profile updated successfully');
     };
     ```
   
   - Notifications tab:
     ```tsx
     const handleSaveNotifications = async () => {
       await adminApi.updateNotificationPreferences({
         emailNotifications,
         smsNotifications
       });
       toast('Notification settings updated');
     };
     ```
   
   - Security tab:
     - Change password (already exists via `/admin/auth/password`)
     - Enable/Disable 2FA
     - Session management

**Verification**:
- Update profile name → verify database change
- Toggle email notifications → check users table
- Change password → confirm new password works
- Enable 2FA → verify field updates

---

### 10. **Header** (Live Notifications)

#### Current State
- Header exists in AdminLayout
- Static notification icon

#### Required Changes

**Frontend** (🔧 To Implement):
1. Create `NotificationProvider.tsx`:
   ```tsx
   const NotificationContext = createContext();
   
   export const NotificationProvider = ({ children }) => {
     const [notifications, setNotifications] = useState([]);
     const [unreadCount, setUnreadCount] = useState(0);
     
     useEffect(() => {
       // Poll every 30 seconds
       const interval = setInterval(async () => {
         const count = await adminApi.getUnreadNotificationCount();
         setUnreadCount(count);
       }, 30000);
       
       return () => clearInterval(interval);
     }, []);
     
     const markAsRead = async (id) => {
       await adminApi.markNotificationAsRead(id);
       setUnreadCount(prev => prev - 1);
     };
     
     return (
       <NotificationContext.Provider value={{ unreadCount, markAsRead }}>
         {children}
       </NotificationContext.Provider>
     );
   };
   ```

2. Update `AdminNavbar.tsx`:
   ```tsx
   const { unreadCount } = useNotifications();
   
   <Button variant="ghost" size="icon">
     <Bell className="w-5 h-5" />
     {unreadCount > 0 && (
       <Badge className="absolute -top-1 -right-1 h-5 w-5 rounded-full">
         {unreadCount}
       </Badge>
     )}
   </Button>
   ```

3. Notification Dropdown:
   - Show recent 5 notifications
   - Click to view all
   - Mark as read button

**Verification**:
- Create new order → notification count increases
- Click notification → opens order details
- Mark as read → count decreases
- Verify polling updates every 30s

---

## Database Schema Changes

### Required Changes for Full Functionality

**IMPORTANT**: While the existing schema supports most features, the following additions are **REQUIRED** for Settings persistence:

1. **Admin Notification Preferences** (**REQUIRED for Settings Module**):
   ```sql
   ALTER TABLE users 
   ADD COLUMN email_notifications BOOLEAN DEFAULT TRUE,
   ADD COLUMN sms_notifications BOOLEAN DEFAULT FALSE;
   ```
   
   **Migration File**: Create `V2__add_notification_preferences.sql` in `backend/src/main/resources/db/migration/`
   
   **Note**: Check if `two_factor_enabled` column exists in users table (per schema CSV it should exist). If not, add:
   ```sql
   ALTER TABLE users ADD COLUMN two_factor_enabled BOOLEAN DEFAULT FALSE;
   ```

2. **Indexes for Performance** (Recommended):
   ```sql
   CREATE INDEX idx_orders_created_at ON orders(created_at);
   CREATE INDEX idx_orders_status ON orders(status);
   CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read);
   CREATE INDEX idx_products_category_id ON products(category_id);
   ```

---

## API Endpoint Summary

### ✅ Existing Endpoints (Already Implemented)
```
# Auth
POST   /admin/auth/login
GET    /admin/auth/me
PUT    /admin/auth/password

# Dashboard
GET    /admin/dashboard/stats

# Orders
GET    /admin/orders
GET    /admin/orders/{id}
PUT    /admin/orders/{id}/status
POST   /admin/orders/{id}/refund
GET    /admin/orders/{id}/timeline

# Products
GET    /admin/products
GET    /admin/products/{id}
POST   /admin/products
PUT    /admin/products/{id}
DELETE /admin/products/{id}
PUT    /admin/products/{id}/stock

# Categories
GET    /admin/categories
POST   /admin/categories
PUT    /admin/categories/{id}
DELETE /admin/categories/{id}

# Users
GET    /admin/users
GET    /admin/users/{id}
PUT    /admin/users/{id}/status
GET    /admin/users/{id}/orders

# Coupons
GET    /admin/coupons
POST   /admin/coupons
PUT    /admin/coupons/{id}
DELETE /admin/coupons/{id}

# Inventory
GET    /admin/inventory
GET    /admin/inventory/low-stock

# Notifications
GET    /admin/notifications
GET    /admin/notifications/unread
PUT    /admin/notifications/{id}/read
PUT    /admin/notifications/read-all

# Reports
GET    /admin/reports/sales
GET    /admin/reports/top-products
GET    /admin/reports/activity-logs
```

### 🆕 Endpoints to Add
```
# Settings
PUT    /admin/settings/profile
PUT    /admin/settings/notifications

# Notifications (Optional - Phase 2)
GET    /admin/notifications/stream (SSE)

# Coupons
POST   /admin/orders/apply-coupon (if not in checkout flow)
```

---

## Implementation Sequence

### **Phase 1: Core Data Integration** (Priority: HIGH)
1. 🔧 Dashboard - Connect to backend stats
2. 🔧 Orders - Full CRUD with filters
3. 🔧 Products - Dynamic categories, edit/delete
4. 🔧 Users - Fix redirect, add actions
5. 🔧 Inventory - Stock management

**Estimated Time**: 2-3 days
**Status**: 🔜 PLANNED (Not yet implemented)

### **Phase 2: Advanced Features** (Priority: MEDIUM)
6. 🔧 Notifications - Polling-based updates (requires OrderCreatedEvent implementation)
7. 🔧 Coupons - Add functionality + order integration
8. 🔧 Reports - Dynamic calculations

**Estimated Time**: 2-3 days
**Status**: 🔜 PLANNED (Not yet implemented)

### **Phase 3: Settings & Real-time** (Priority: LOW)
9. 🔧 Settings - Persist all preferences (requires schema migration)
10. 🔧 Header - Live notifications display
11. 🔮 WebSocket/SSE (Optional - Future enhancement)

**Estimated Time**: 1-2 days
**Status**: 🔜 PLANNED (Not yet implemented)

---

## Testing Checklist

### Dashboard
- [ ] Total orders count is accurate
- [ ] Total customers count is accurate
- [ ] Recent orders display latest 10
- [ ] Stats refresh on mount

### Orders
- [ ] Filter by status works
- [ ] Search by order number works
- [ ] Status update persists to database
- [ ] Refund modal processes refunds
- [ ] Order timeline displays correctly

### Products
- [ ] Category dropdown loads from database
- [ ] Edit product pre-fills data correctly
- [ ] Delete shows two-step confirmation
- [ ] Category name displays in list
- [ ] Create product saves to database

### Inventory
- [ ] Stock number displays (not "In Stock")
- [ ] Add stock increases count
- [ ] Decrease stock reduces count
- [ ] Category displays correctly
- [ ] Low stock alert works

### Coupons
- [ ] List displays all coupons from database
- [ ] Add coupon form validates correctly
- [ ] Coupon applies discount at checkout
- [ ] Usage count increments
- [ ] Expired coupons are rejected

### Users
- [ ] Actions don't redirect to login
- [ ] Activate/deactivate updates status
- [ ] User details page loads
- [ ] Order history displays
- [ ] Search and filter work

### Reports
- [ ] Revenue calculates correctly
- [ ] Order count is accurate
- [ ] AOV = Revenue / Orders
- [ ] Sales by category sums correctly
- [ ] Date range filter works

### Notifications
- [ ] New order creates notification
- [ ] Unread count displays in header
- [ ] Mark as read works
- [ ] Notifications list loads
- [ ] Polling updates count

### Settings
- [ ] Profile update saves to database
- [ ] Notification preferences persist
- [ ] Password change works
- [ ] 2FA toggle updates

---

## Success Criteria

✅ **100% Dynamic Data**: No hardcoded values in frontend  
✅ **Real-time Updates**: Notifications appear within 30 seconds  
✅ **Full CRUD**: All entities support Create, Read, Update, Delete  
✅ **Data Consistency**: Frontend displays match database state  
✅ **User Experience**: Smooth interactions, proper loading states  
✅ **Error Handling**: Meaningful messages for failed operations  
✅ **Security**: All actions require authentication  
✅ **Performance**: Pages load under 2 seconds  

---

## Risk Mitigation

### Risk: Category Hardcoded Values
**Solution**: Create `useCategories` hook, fetch on mount

### Risk: Notification Overwhelm
**Solution**: Implement pagination, mark all as read

### Risk: Stock Concurrency Issues
**Solution**: Optimistic locking in database

### Risk: API Authentication Failures
**Solution**: Implement token refresh, proper error handling

---

## Notes

- Current backend is production-ready for most features
- Focus on frontend integration and UX polish
- Consider React Query for caching and optimistic updates
- Add loading skeletons for better perceived performance
- Implement proper error boundaries

---

**Document Version**: 1.0  
**Last Updated**: October 24, 2025  
**Status**: Ready for Implementation ✅
