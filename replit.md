# E-Commerce Admin Dashboard Project

## Overview
This is a full-stack e-commerce admin dashboard with a React frontend and Spring Boot backend. It provides a comprehensive interface for managing orders, products, users, inventory, coupons, and analytics.

## Tech Stack

### Frontend
- **Framework**: React 18.3.1
- **Build Tool**: Vite 6.3.5
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **UI Components**: Radix UI primitives
- **Routing**: React Router DOM
- **Charts**: Recharts
- **Form Management**: React Hook Form
- **Icons**: Lucide React

### Backend
- **Framework**: Spring Boot 3.2.5
- **Language**: Java 17
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **Security**: JWT Authentication
- **Build Tool**: Maven 3.9.6
- **Logging**: Logback with AOP aspect logging
- **API Documentation**: SpringDoc OpenAPI (Swagger)

## Project Structure

### Frontend (`/src`)
```
src/
├── api/              # API integration
├── components/       # React components
│   ├── admin/       # Admin-specific components
│   ├── figma/       # Figma-related components
│   └── ui/          # Reusable UI components
├── contexts/        # React context providers
├── hooks/           # Custom React hooks
├── pages/           # Page components
└── styles/          # Global styles
```

### Backend (`/backend`)
```
backend/src/main/java/com/ecommerce/admin/
├── AdminApplication.java          # Spring Boot main application
├── config/                        # Configuration classes
│   └── CorsConfig.java
├── controller/                    # REST API controllers
│   ├── AuthController.java
│   ├── CategoryController.java
│   ├── CouponController.java
│   ├── DashboardController.java
│   ├── InventoryController.java
│   ├── NotificationController.java
│   ├── OrderController.java
│   ├── ProductController.java
│   ├── ReportController.java
│   ├── SettingController.java
│   └── UserController.java
├── service/                       # Business logic services
├── repository/                    # Database repositories
├── model/                         # JPA entities
├── dto/                          # Data transfer objects
│   ├── request/
│   └── response/
├── security/                     # JWT security configuration
├── exception/                    # Custom exceptions
├── aspect/                       # AOP logging aspects
└── util/                        # Utility classes
```

## Key Features
- **Authentication**: Admin login with authentication guards
- **Dashboard**: Overview with sales charts and statistics
- **Order Management**: View, filter, and manage orders
- **Product Management**: CRUD operations for products
- **User Management**: Manage user accounts
- **Inventory Management**: Track and manage stock
- **Coupons**: Create and manage promotional codes
- **Reports**: Analytics and reporting
- **Settings**: System configuration
- **Notifications**: Alert system

## Development

### Running the Frontend
The frontend runs on port 5000 and is configured for the Replit environment:
- Host: 0.0.0.0 (accessible from anywhere)
- HMR configured for Replit's proxy setup
- WebSocket support for hot module replacement
- Command: `npm run dev`

### Running the Backend
The backend runs on port 8080:
- Development profile includes SQL logging and debug output
- JWT authentication with configurable secret
- CORS enabled for frontend access
- Command: `cd backend && mvn spring-boot:run`

### Environment Variables
Required for production:
- `JWT_SECRET` - Secret key for JWT token generation (required)
- `DATABASE_URL` - MySQL database connection URL
- `DATABASE_USERNAME` - Database username
- `DATABASE_PASSWORD` - Database password

Optional (with defaults):
- `JWT_EXPIRATION` - Token expiration in milliseconds (default: 86400000 = 24 hours)
- `JWT_REFRESH_EXPIRATION` - Refresh token expiration (default: 604800000 = 7 days)

## Recent Changes (October 22, 2025)

### Frontend Import & Setup
- Imported React frontend from GitHub
- Added TypeScript configuration (tsconfig.json)
- Configured Vite for Replit environment (port 5000, host 0.0.0.0)
- Set up workflow for development server
- Added .gitignore for Node.js projects

### Backend Completion
- ✅ Created AdminApplication.java - Spring Boot main application class
- ✅ Created application.yml with dev/prod profiles for database, JWT, logging, CORS
- ✅ Created utility classes: DateUtil, ValidationUtil
- ✅ Created AdminActivityAspect for AOP-based audit logging
- ✅ Implemented CategoryController and CategoryService
- ✅ Fixed compilation errors: added REFUNDED status, extracted DTOs, added missing methods
- ✅ Removed security vulnerabilities: eliminated hardcoded credentials
- ✅ Backend compiles successfully with BUILD SUCCESS (86 source files)

### Security Configuration
- JWT-only authentication (no basic auth in production)
- JWT secret must be provided via environment variable in production
- Development profile includes a default JWT secret for local testing only
- CORS configured for Replit environment

## API Architecture

### REST API Endpoints
Base path: `/api/v1`

- **Authentication**: POST `/auth/login`, POST `/auth/refresh`
- **Dashboard**: GET `/dashboard/stats`, GET `/dashboard/sales-data`
- **Orders**: GET/POST `/orders`, GET/PUT `/orders/{id}`, PUT `/orders/{id}/status`
- **Products**: GET/POST `/products`, GET/PUT/DELETE `/products/{id}`, POST `/products/{id}/stock`
- **Categories**: GET/POST `/categories`, GET/PUT/DELETE `/categories/{id}`
- **Users**: GET/POST `/users`, GET/PUT/DELETE `/users/{id}`, PUT `/users/{id}/toggle-status`
- **Coupons**: GET/POST `/coupons`, GET/PUT/DELETE `/coupons/{id}`, PUT `/coupons/{id}/toggle-status`
- **Inventory**: GET `/inventory`, GET `/inventory/low-stock`, PUT `/inventory/{id}/adjust`
- **Reports**: GET `/reports/sales`, GET `/reports/products`, GET `/reports/revenue`
- **Notifications**: GET/POST `/notifications`, GET/PUT `/notifications/{id}`, PUT `/notifications/{id}/read`
- **Settings**: GET/PUT `/settings/{key}`

### Database Models
- User, Address, Product, Category
- Order, OrderItem, OrderStatusHistory, OrderRefund
- Coupon, Notification, Setting
- All with audit fields (createdAt, updatedAt)

## Routes
- `/admin/login` - Admin login page
- `/admin/dashboard` - Main dashboard
- `/admin/notifications` - Notifications
- `/admin/orders` - Order list
- `/admin/orders/:id` - Order details
- `/admin/products` - Product list
- `/admin/products/:id/edit` - Edit product
- `/admin/coupons` - Coupon management
- `/admin/users` - User management
- `/admin/inventory` - Inventory management
- `/admin/settings` - Settings
- `/admin/reports` - Reports and analytics
