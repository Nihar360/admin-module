# Quick Reference Guide

## 🚀 Starting the Application

### Automatic (Recommended)

**Linux/Mac:**
```bash
./run-local.sh
```

**Windows:**
```cmd
run-local.bat
```

### Manual

**Terminal 1 - Backend:**
```bash
cd backend
java -jar target/admin-backend-1.0.0.jar
```

**Terminal 2 - Frontend:**
```bash
npm run dev
```

## 🔑 Login Credentials

- **Email:** admin@example.com
- **Password:** admin123

## 🌐 Application URLs

- **Frontend:** http://localhost:5000
- **Backend API:** http://localhost:8080/api/v1
- **Swagger UI:** http://localhost:8080/api/v1/swagger-ui.html
- **Health Check:** http://localhost:8080/api/v1/actuator/health

## 📊 Database Commands

### Access MySQL
```bash
mysql -u root -p ecommerce_db
```

### Show All Tables
```sql
SHOW TABLES;
```

### View Users
```sql
SELECT id, email, full_name, role, is_active FROM users;
```

### View Products
```sql
SELECT id, name, price, stock_quantity, is_active FROM products;
```

### View Orders
```sql
SELECT id, order_number, status, total, created_at FROM orders;
```

### Create New Admin User
```sql
INSERT INTO users (email, password, full_name, mobile, role, created_at, updated_at, is_active, two_factor_enabled)
VALUES (
  'newadmin@example.com',
  '$2a$10$vU5Hy8HlQWlOAhbHOEpz5.0QN7OyLG5xMjBVXH5xBx5bZCKGQ8vEm',
  'New Admin',
  '0987654321',
  'ADMIN',
  NOW(),
  NOW(),
  1,
  0
);
```
> Password for new user will be: admin123

### Reset Admin Password
```sql
UPDATE users 
SET password = '$2a$10$vU5Hy8HlQWlOAhbHOEpz5.0QN7OyLG5xMjBVXH5xBx5bZCKGQ8vEm'
WHERE email = 'admin@example.com';
```
> Resets password to: admin123

## 🛠️ Common Development Commands

### Frontend

```bash
# Install dependencies
npm install

# Start dev server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

### Backend

```bash
# Build project
cd backend
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run with Maven
mvn spring-boot:run

# Run JAR file
java -jar target/admin-backend-1.0.0.jar

# Run tests
mvn test

# Clean build artifacts
mvn clean
```

## 🔍 Checking Services

### Check if MySQL is running

**Linux:**
```bash
sudo systemctl status mysql
```

**Mac:**
```bash
brew services list | grep mysql
```

**Windows:**
```cmd
sc query MySQL
```

### Check if Backend is running
```bash
curl http://localhost:8080/api/v1/actuator/health
```

### Check if Frontend is running
```bash
curl http://localhost:5000
```

### Find process using a port

**Linux/Mac:**
```bash
lsof -i :8080
lsof -i :5000
```

**Windows:**
```cmd
netstat -ano | findstr :8080
netstat -ano | findstr :5000
```

## 📝 Environment Variables

### Required Variables
```bash
DB_USERNAME=root
DB_PASSWORD=your_password
```

### Optional Variables
```bash
DATABASE_URL=jdbc:mysql://localhost:3306/ecommerce_db
JWT_SECRET=your_secret_key
JWT_EXPIRATION=86400000
PORT=8080
VITE_API_URL=http://localhost:8080/api/v1
```

### Set Environment Variables

**Linux/Mac:**
```bash
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
```

**Windows CMD:**
```cmd
set DB_USERNAME=root
set DB_PASSWORD=yourpassword
```

**Windows PowerShell:**
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="yourpassword"
```

## 🐛 Troubleshooting Quick Fixes

### Backend won't start - Database error
```bash
# Check MySQL is running
sudo systemctl start mysql  # Linux
brew services start mysql   # Mac
net start MySQL             # Windows

# Check database exists
mysql -u root -p -e "SHOW DATABASES;"
```

### Backend won't start - Port in use
```bash
# Find and kill process on port 8080
# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Frontend won't connect to backend
```bash
# Check backend is running
curl http://localhost:8080/api/v1/actuator/health

# Check CORS settings in backend/src/main/resources/application.yml
```

### Can't login
```bash
# Verify admin user exists
mysql -u root -p ecommerce_db -e "SELECT * FROM users WHERE email='admin@example.com';"

# Re-create admin user if needed (see Database Commands above)
```

### Clear MySQL data and start fresh
```bash
mysql -u root -p -e "DROP DATABASE ecommerce_db; CREATE DATABASE ecommerce_db;"
# Then restart backend to recreate tables
```

## 📦 Project Files

- `LOCAL_SETUP.md` - Detailed setup instructions
- `.env.example` - Environment variables template
- `run-local.sh` - Auto-start script (Linux/Mac)
- `run-local.bat` - Auto-start script (Windows)
- `backend/src/main/resources/application.yml` - Backend config
- `backend/logs/application.log` - Backend logs
- `package.json` - Frontend dependencies

## 🔄 Updating Dependencies

### Frontend
```bash
# Update all dependencies
npm update

# Update specific package
npm update <package-name>

# Check for outdated packages
npm outdated
```

### Backend
```bash
cd backend
# Update Maven dependencies
mvn versions:display-dependency-updates
```

## 📊 Monitoring

### View Backend Logs
```bash
tail -f backend/logs/application.log
```

### View Database Logs (MySQL)
```bash
# Linux
sudo tail -f /var/log/mysql/error.log

# Mac
tail -f /usr/local/var/mysql/*.err
```

### Monitor Backend Memory
```bash
# Get backend process ID
ps aux | grep admin-backend

# Monitor with top/htop
top -p <PID>
```

## 🎯 User Roles

- **CUSTOMER** - Regular users (customers)
- **ADMIN** - Admin users (can manage products, orders, etc.)
- **SUPER_ADMIN** - Super admin (full access)

## 📱 API Testing

### Using cURL

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'
```

**Get Products (with token):**
```bash
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Using Swagger UI

Navigate to: http://localhost:8080/api/v1/swagger-ui.html

1. Click "Authorize" button
2. Login to get JWT token
3. Test all API endpoints interactively

## 🔒 Security Notes

- Change default admin password in production
- Never commit `.env` file to version control
- Use strong JWT secret in production
- Enable HTTPS in production
- Regularly update dependencies

---

For more detailed information, see [LOCAL_SETUP.md](LOCAL_SETUP.md)
