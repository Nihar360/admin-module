# E-Commerce Admin Dashboard

## Project Overview
Full-stack e-commerce admin dashboard with React (TypeScript + Vite) frontend and Spring Boot backend, connected to MySQL database. All admin operations sync with the live database - no mock data.

## Current Status
- **Last Updated**: October 23, 2025
- **Status**: Backend and database fully configured and running. Frontend connected to real API.
- **Environment**: Replit Cloud Development Environment

## Technology Stack

### Frontend
- **Framework**: React 18.3.1 with TypeScript
- **Build Tool**: Vite 6.3.5
- **UI Library**: Radix UI components + Tailwind CSS
- **Routing**: React Router DOM
- **HTTP Client**: Axios
- **State Management**: React Context API (AdminAuthContext)
- **Port**: 5000

### Backend
- **Framework**: Spring Boot 3.x (Java 21)
- **Database**: MySQL 5.7 (MariaDB 10.11.13 compatible)
- **ORM**: Spring Data JPA with Hibernate
- **Authentication**: JWT (JSON Web Tokens)
- **Security**: Spring Security with BCrypt password encryption
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Port**: 8080
- **Context Path**: `/api/v1`

### Database
- **Type**: MySQL/MariaDB
- **Database Name**: `ecommerce_db`
- **Connection**: TCP/IP on 127.0.0.1:3306
- **Storage**: Local socket at `/home/runner/workspace/mysql/mysql.sock`

## System Architecture

### Workflows (Auto-running Services)
1. **MySQL** - Database server (MariaDB)
2. **Backend** - Spring Boot application on port 8080
3. **Server** - Vite dev server on port 5000

### API Endpoints
Base URL: `http://[REPLIT_DOMAIN]:8080/api/v1`

#### Authentication
- POST `/admin/auth/login` - Admin login (public)
- GET `/admin/auth/me` - Get current user info

#### Dashboard
- GET `/admin/dashboard/stats` - Get dashboard statistics
- GET `/admin/dashboard/sales` - Get sales data

#### Products
- GET `/admin/products` - List all products (with pagination & filters)
- GET `/admin/products/{id}` - Get product by ID
- POST `/admin/products` - Create new product
- PUT `/admin/products/{id}` - Update product
- DELETE `/admin/products/{id}` - Delete product
- PUT `/admin/products/{id}/stock` - Adjust stock levels

#### Orders
- GET `/admin/orders` - List all orders (with pagination & filters)
- GET `/admin/orders/{id}` - Get order by ID
- PUT `/admin/orders/{id}/status` - Update order status

#### Inventory
- GET `/admin/inventory` - Get inventory list
- GET `/admin/inventory/low-stock` - Get low stock products

#### Coupons
- GET `/admin/coupons` - List all coupons
- POST `/admin/coupons` - Create new coupon
- PUT `/admin/coupons/{id}` - Update coupon
- DELETE `/admin/coupons/{id}` - Delete coupon

#### Users
- GET `/admin/users` - List all users
- GET `/admin/users/{id}` - Get user by ID

## Database Schema

### Tables
- `users` - Admin and customer accounts
- `products` - Product catalog
- `categories` - Product categories
- `orders` - Customer orders
- `order_items` - Order line items
- `coupons` - Discount coupons
- `addresses` - Shipping addresses
- `cart_items` - Shopping cart items

### Admin User Credentials
**Email**: `admin@example.com`  
**Password**: `admin123`  
**Role**: ADMIN

## Environment Variables

The following secrets are configured in Replit:
- `DATABASE_USERNAME` - MySQL database user (root)
- `DATABASE_PASSWORD` - MySQL root password
- `JWT_SECRET` - Secret key for JWT token generation

### Frontend Environment
Create `.env.local`:
```
VITE_API_URL=http://[YOUR_REPLIT_DOMAIN]:8080/api/v1
```

## File Structure

```
/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/ecommerce/admin/
│   │       ├── config/        # Security, CORS, etc.
│   │       ├── controller/    # REST API controllers
│   │       ├── service/       # Business logic
│   │       ├── repository/    # JPA repositories
│   │       ├── model/         # Entity classes
│   │       ├── dto/           # Data transfer objects
│   │       └── security/      # JWT & auth logic
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml               # Maven dependencies
├── src/                       # React frontend
│   ├── api/                  # API client & types
│   │   ├── apiClient.ts     # Axios configuration
│   │   └── adminApi.ts      # API functions
│   ├── components/          # React components
│   │   ├── admin/
│   │   │   ├── layout/
│   │   │   ├── dashboard/
│   │   │   ├── orders/
│   │   │   ├── products/
│   │   │   └── shared/
│   │   └── ui/             # Shadcn UI components
│   ├── contexts/           # React contexts
│   │   └── AdminAuthContext.tsx
│   ├── hooks/              # Custom React hooks
│   │   ├── useAdminDashboard.ts
│   │   ├── useAdminOrders.ts
│   │   └── useAdminProducts.ts
│   ├── pages/              # Page components
│   │   ├── AdminDashboard.tsx
│   │   ├── AdminLogin.tsx
│   │   ├── ProductList.tsx
│   │   ├── ProductForm.tsx
│   │   ├── OrderList.tsx
│   │   ├── OrderDetails.tsx
│   │   ├── InventoryManagement.tsx
│   │   ├── CouponList.tsx
│   │   └── UserList.tsx
│   └── App.tsx             # Main app component
├── mysql/                   # MySQL data directory
├── start-mysql.sh          # MySQL startup script
└── replit.md               # This file
```

## Features Implemented

### ✅ User Management
- Admin authentication with JWT
- BCrypt password encryption
- Role-based access control

### ✅ Product Management
- Create, read, update, delete products
- Product categories
- Stock tracking
- Product search and filtering
- Image URL storage

### ✅ Order Management
- View all orders with pagination
- Order status updates
- Order details view
- Filter by status
- Search orders

### ✅ Inventory Management
- Real-time stock levels
- Stock adjustments (add/subtract)
- Low stock alerts
- Category-based filtering

### ✅ Coupon Management
- Create discount coupons
- Percentage and fixed amount discounts
- Minimum purchase requirements
- Usage limits and tracking
- Expiration dates

### ✅ Dashboard
- Total orders and customers statistics
- Revenue tracking
- Sales trends
- Low stock alerts
- Recent orders view

## Development Notes

### Starting the Application

1. All workflows auto-start in Replit
2. MySQL starts first and creates the database
3. Backend connects to MySQL on startup
4. Frontend connects to backend API

### Testing the System

1. **Login**: Navigate to `/login` and use admin credentials
2. **Dashboard**: View statistics and recent orders
3. **Products**: Manage product catalog
4. **Orders**: View and update order statuses
5. **Inventory**: Track and adjust stock levels
6. **Coupons**: Create and manage discount codes

### Backend Build

The backend JAR is pre-built at:
```
backend/target/admin-backend-1.0.0.jar
```

To rebuild:
```bash
cd backend
mvn clean package -DskipTests
```

### Database Management

Connect to MySQL:
```bash
mysql -u root -p${DATABASE_PASSWORD} -h 127.0.0.1 ecommerce_db
```

View tables:
```sql
SHOW TABLES;
DESCRIBE users;
SELECT * FROM users;
```

### API Testing

Test login endpoint:
```bash
curl -X POST http://localhost:8080/api/v1/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'
```

### Known Issues & Solutions

1. **CORS Errors**: CORS is configured in Spring Security to allow frontend domain
2. **JWT Expiration**: Tokens expire after configured time (check application.properties)
3. **Database Connection**: Uses TCP/IP connection to localhost:3306

## Next Steps / TODO

- [ ] Add product image upload functionality
- [ ] Implement customer management
- [ ] Add sales reports and analytics
- [ ] Export data to CSV/Excel
- [ ] Email notifications for orders
- [ ] Inventory low-stock alerts
- [ ] Bulk operations for products
- [ ] Advanced filtering and sorting
- [ ] Deploy to production environment

## User Preferences

- **Database**: MySQL (required, no PostgreSQL)
- **Mock Data**: None - all data from live database
- **Authentication**: JWT-based with secure password hashing
- **Real-time Sync**: All frontend operations sync immediately with backend

## Running Locally

This project is fully configured to run on your local machine. See detailed instructions in:
- **[LOCAL_SETUP.md](LOCAL_SETUP.md)** - Complete setup guide
- **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Quick commands reference
- **[README.md](README.md)** - Project overview

### Quick Start Scripts
- **Linux/Mac**: `./run-local.sh` - Auto-setup and start all services
- **Windows**: `run-local.bat` - Auto-setup and start all services

### Local Requirements
- Java 19+ (backend)
- Maven 3.6+ (build tool)
- Node.js 18+ (frontend)
- MySQL 8+ or MariaDB 10.11+ (database)

### Environment Configuration
Copy `.env.example` to `.env` and set your MySQL credentials:
```
DB_USERNAME=root
DB_PASSWORD=your_password
```

All configuration already supports both Replit and local environments with environment variable fallbacks.

## Recent Changes

### October 23, 2025
- **Import Migration Completed**: Project migrated to Replit environment
- Installed all npm dependencies
- Set up database credentials as Replit secrets
- Fixed MySQL startup script to handle existing databases
- Created initial admin user (admin@example.com / admin123)
- All three workflows (Frontend, Backend, MySQL) running successfully
- **Local Development Ready**: Created comprehensive local setup documentation
  - Added LOCAL_SETUP.md with step-by-step instructions
  - Added run-local.sh and run-local.bat startup scripts
  - Added .env.example for environment variables
  - Added QUICK_REFERENCE.md for common commands
  - Updated README.md with project overview
