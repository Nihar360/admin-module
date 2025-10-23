# E-Commerce Admin Dashboard

A full-stack e-commerce admin dashboard with React frontend and Spring Boot backend, featuring comprehensive product, order, and user management capabilities.

## 🚀 Quick Start

### Running on Replit

The project is already configured and running on Replit with all necessary services:
- Frontend (React + Vite) on port 5000
- Backend (Spring Boot) on port 8080
- MySQL Database on port 3306

Simply click the "Run" button to start all services.

### Running Locally

For detailed local setup instructions, see **[LOCAL_SETUP.md](LOCAL_SETUP.md)**

**Quick start for local development:**

1. **Prerequisites**: Java 19+, Maven, Node.js 18+, MySQL 8+

2. **Easy setup** (Linux/Mac):
   ```bash
   chmod +x run-local.sh
   ./run-local.sh
   ```

3. **Easy setup** (Windows):
   ```cmd
   run-local.bat
   ```

4. **Manual setup**:
   ```bash
   # Set up environment
   cp .env.example .env
   # Edit .env with your MySQL credentials
   
   # Install frontend dependencies
   npm install
   
   # Start backend
   cd backend
   java -jar target/admin-backend-1.0.0.jar
   
   # In another terminal, start frontend
   npm run dev
   ```

5. **Access the application**:
   - Frontend: http://localhost:5000
   - Backend API: http://localhost:8080/api/v1
   - Swagger UI: http://localhost:8080/api/v1/swagger-ui.html

6. **Login**:
   - Email: `admin@example.com`
   - Password: `admin123`

## 📋 Features

### Admin Features
- 🔐 **Authentication & Authorization** - JWT-based secure login with role-based access control
- 📦 **Product Management** - Add, edit, delete products with images, categories, and inventory
- 📊 **Dashboard Analytics** - Sales overview, revenue tracking, and key metrics
- 🛍️ **Order Management** - View, process, and track customer orders
- 👥 **User Management** - Manage customers and admin users
- 🎟️ **Coupon Management** - Create and manage discount coupons
- 📱 **Notifications** - Real-time notification system
- 📈 **Reports** - Generate sales and inventory reports
- 🏷️ **Category Management** - Organize products into categories
- ⚙️ **Settings** - Configure application settings

### Technical Features
- 🎨 **Modern UI** - Built with React, TypeScript, and Tailwind CSS
- 🔄 **Real-time Updates** - Live data synchronization
- 📱 **Responsive Design** - Works on desktop, tablet, and mobile
- 🔍 **Advanced Search** - Filter and search across all entities
- 📝 **Activity Logging** - Track admin actions and changes
- 🔒 **Security** - BCrypt password hashing, JWT tokens, CORS protection
- 📚 **API Documentation** - Integrated Swagger UI for API exploration

## 🏗️ Technology Stack

### Frontend
- **React 18** - Modern UI library
- **TypeScript** - Type-safe development
- **Vite** - Fast build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **Radix UI** - Accessible component primitives
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **Recharts** - Data visualization

### Backend
- **Spring Boot 3.x** - Java application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database abstraction
- **Hibernate** - ORM framework
- **JWT** - Token-based authentication
- **MySQL** - Relational database
- **Swagger/OpenAPI** - API documentation
- **Lombok** - Reduce boilerplate code

## 📁 Project Structure

```
.
├── backend/                    # Spring Boot backend
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/ecommerce/admin/
│   │       │       ├── config/        # Configuration classes
│   │       │       ├── controller/    # REST controllers
│   │       │       ├── model/         # Entity classes
│   │       │       ├── repository/    # Data repositories
│   │       │       ├── service/       # Business logic
│   │       │       └── security/      # Security config
│   │       └── resources/
│   │           └── application.yml    # App configuration
│   └── target/
│       └── admin-backend-1.0.0.jar   # Executable JAR
│
├── src/                        # React frontend
│   ├── api/                   # API client
│   ├── components/            # React components
│   │   ├── ui/               # Reusable UI components
│   │   └── admin/            # Admin-specific components
│   ├── pages/                # Page components
│   ├── contexts/             # React contexts
│   ├── hooks/                # Custom hooks
│   └── styles/               # Global styles
│
├── .env.example              # Environment variables template
├── LOCAL_SETUP.md           # Local setup guide
├── run-local.sh             # Local startup script (Linux/Mac)
├── run-local.bat            # Local startup script (Windows)
└── package.json             # Frontend dependencies
```

## 🔑 Admin Credentials

**Default Admin Account:**
- Email: `admin@example.com`
- Password: `admin123`

**⚠️ Important:** Change the default password after first login in production!

## 🗄️ Database Schema

The application uses the following main tables:
- `users` - User accounts (customers and admins)
- `products` - Product catalog
- `categories` - Product categories
- `orders` - Customer orders
- `order_items` - Order line items
- `coupons` - Discount coupons
- `notifications` - User notifications
- `admin_activity_logs` - Admin action tracking

For detailed schema, see the attached database schema file.

## 🔧 Configuration

### Environment Variables

Key environment variables (see `.env.example`):

```bash
# Database
DB_USERNAME=root
DB_PASSWORD=your_password
DATABASE_URL=jdbc:mysql://localhost:3306/ecommerce_db

# JWT
JWT_SECRET=your_secret_key
JWT_EXPIRATION=86400000

# Server
PORT=8080
```

### Application Profiles

- **dev** - Development profile with debug logging
- **prod** - Production profile with optimized settings

Activate profile: `spring.profiles.active=dev` in `application.yml`

## 📚 API Documentation

Access the interactive API documentation at:
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v1/api-docs

## 🛠️ Development

### Build Backend
```bash
cd backend
mvn clean install
```

### Run Backend
```bash
cd backend
mvn spring-boot:run
```

### Build Frontend
```bash
npm run build
```

### Run Frontend Dev Server
```bash
npm run dev
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
npm test
```

## 📦 Deployment

### Backend
The backend is packaged as an executable JAR:
```bash
cd backend
mvn clean package -DskipTests
java -jar target/admin-backend-1.0.0.jar
```

### Frontend
Build the frontend for production:
```bash
npm run build
# Files will be in dist/ folder
```

Serve with any static file server (nginx, Apache, etc.)

## 🐛 Troubleshooting

See **[LOCAL_SETUP.md](LOCAL_SETUP.md)** for detailed troubleshooting guide.

Common issues:
- **Port conflicts**: Backend uses 8080, Frontend uses 5000
- **Database connection**: Ensure MySQL is running
- **CORS errors**: Check `allowed-origins` in `application.yml`

## 📄 License

This project is available for use under standard licensing terms.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests.

## 📞 Support

For issues or questions:
1. Check the logs: `backend/logs/application.log`
2. Review console output
3. Check browser console for frontend errors

---

**Original Design**: This is a code implementation based on the Figma design available at:
https://www.figma.com/design/VH2hROiymY3hEDwlTjnlON/Admin-Dashboard-Project-Code
