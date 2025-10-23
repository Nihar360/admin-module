# Local Development Setup Guide

This guide will help you run the E-commerce Admin Dashboard on your local machine.

## Prerequisites

Before you begin, make sure you have the following installed:

1. **Java 19** or higher (for the backend)
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Verify: `java -version`

2. **Maven 3.6+** (for building the backend)
   - Download from: https://maven.apache.org/download.cgi
   - Verify: `mvn -version`

3. **Node.js 18+** and **npm** (for the frontend)
   - Download from: https://nodejs.org/
   - Verify: `node -v` and `npm -v`

4. **MySQL 8.0+** (for the database)
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Or use MariaDB 10.11+: https://mariadb.org/download/

## Quick Start

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd <your-project-directory>
```

### 2. Set Up MySQL Database

**Option A: Using MySQL Command Line**

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE ecommerce_db;

# Create user (optional, or use root)
CREATE USER 'ecommerce_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ecommerce_db.* TO 'ecommerce_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

**Option B: Using the included script** (Linux/Mac only)

```bash
chmod +x start-mysql.sh
./start-mysql.sh
```

### 3. Create Admin User

After the database is set up and the backend has run once (it will auto-create tables), create an admin user:

```bash
mysql -u root -p ecommerce_db
```

Then run this SQL:

```sql
INSERT INTO users (email, password, full_name, mobile, role, created_at, updated_at, is_active, two_factor_enabled)
VALUES (
  'admin@example.com',
  '$2a$10$vU5Hy8HlQWlOAhbHOEpz5.0QN7OyLG5xMjBVXH5xBx5bZCKGQ8vEm',
  'Admin User',
  '1234567890',
  'ADMIN',
  NOW(),
  NOW(),
  1,
  0
);
```

**Admin Credentials:**
- Email: `admin@example.com`
- Password: `admin123`

### 4. Configure Environment Variables

Create a `.env` file in the project root (or set system environment variables):

```bash
# Database Configuration
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
DATABASE_URL=jdbc:mysql://localhost:3306/ecommerce_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC

# JWT Configuration (optional, defaults are provided)
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Server Configuration (optional)
PORT=8080
```

For **Windows**, set environment variables using:
```cmd
set DB_USERNAME=root
set DB_PASSWORD=your_mysql_password
```

For **Linux/Mac**, export them:
```bash
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
```

### 5. Run the Backend

**Option A: Using the pre-built JAR**

```bash
cd backend
java -jar target/admin-backend-1.0.0.jar
```

**Option B: Build and run with Maven**

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will start on **http://localhost:8080**

### 6. Run the Frontend

In a new terminal:

```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will start on **http://localhost:5000**

### 7. Access the Application

Open your browser and navigate to:
- **Frontend:** http://localhost:5000
- **Backend API:** http://localhost:8080/api/v1
- **Swagger UI:** http://localhost:8080/api/v1/swagger-ui.html

Login with:
- **Email:** admin@example.com
- **Password:** admin123

## Project Structure

```
.
├── backend/                 # Spring Boot backend
│   ├── src/
│   │   └── main/
│   │       ├── java/       # Java source code
│   │       └── resources/  # Configuration files
│   └── target/             # Compiled JAR file
├── src/                    # React frontend
│   ├── api/               # API client
│   ├── components/        # React components
│   ├── pages/            # Page components
│   └── hooks/            # Custom hooks
└── package.json          # Frontend dependencies
```

## Troubleshooting

### Backend won't start

**Problem:** Database connection error
```
Solution: 
1. Check MySQL is running: sudo systemctl status mysql (Linux) or check Services (Windows)
2. Verify database credentials in environment variables
3. Ensure database exists: mysql -u root -p -e "SHOW DATABASES;"
```

**Problem:** Port 8080 already in use
```
Solution: 
1. Find process using port: lsof -i :8080 (Mac/Linux) or netstat -ano | findstr :8080 (Windows)
2. Kill the process or change PORT environment variable
```

**Problem:** JWT_SECRET error
```
Solution: Set the JWT_SECRET environment variable or use the default from application-dev.yml
```

### Frontend won't start

**Problem:** Port 5000 already in use
```
Solution: Vite will automatically use the next available port (5001, 5002, etc.)
```

**Problem:** Cannot connect to backend
```
Solution: 
1. Ensure backend is running on http://localhost:8080
2. Check browser console for CORS errors
3. Verify VITE_API_URL in .env (if set) points to http://localhost:8080/api/v1
```

### Database issues

**Problem:** Tables not created
```
Solution: 
1. The backend uses Hibernate with ddl-auto: update
2. Tables will be created automatically on first run
3. Check logs for SQL errors
```

**Problem:** Can't login with admin credentials
```
Solution: 
1. Verify admin user was created: mysql -u root -p -e "SELECT * FROM ecommerce_db.users;"
2. Re-insert the admin user using the SQL above
3. Check backend logs for authentication errors
```

## Building for Production

### Backend
```bash
cd backend
mvn clean package -DskipTests
# JAR file will be in target/admin-backend-1.0.0.jar
```

### Frontend
```bash
npm run build
# Build files will be in dist/
```

## Additional Commands

### Backend
```bash
# Run tests
mvn test

# Clean build
mvn clean

# Skip tests during build
mvn clean install -DskipTests
```

### Frontend
```bash
# Run development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

## Default Configuration

The application uses these defaults (can be overridden with environment variables):

- **Backend Port:** 8080
- **Frontend Port:** 5000
- **Database:** MySQL on localhost:3306
- **Database Name:** ecommerce_db
- **API Base Path:** /api/v1
- **JWT Expiration:** 24 hours
- **Refresh Token Expiration:** 7 days

## Support

For issues or questions:
1. Check the logs in `backend/logs/application.log`
2. Review the backend console output
3. Check the browser console for frontend errors
