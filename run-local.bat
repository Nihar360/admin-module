@echo off
REM Local Development Startup Script for Windows
REM This script helps you run the e-commerce admin dashboard locally

echo ========================================
echo E-Commerce Admin Dashboard - Local Setup
echo ========================================
echo.

REM Check if MySQL is running
echo Checking MySQL...
sc query MySQL >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] MySQL service is not running
    echo Please start MySQL from Services or run: net start MySQL
    pause
    exit /b 1
)
echo [OK] MySQL is running
echo.

REM Check if Java is installed
echo Checking Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed
    echo Please install Java 19+ from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
echo [OK] Java is installed
echo.

REM Check if Node.js is installed
echo Checking Node.js...
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js is not installed
    echo Please install Node.js from: https://nodejs.org/
    pause
    exit /b 1
)
echo [OK] Node.js is installed
echo.

REM Load environment variables from .env file
if exist .env (
    for /f "usebackq tokens=*" %%a in (".env") do (
        set %%a
    )
) else (
    echo [WARNING] No .env file found
    echo Creating .env from .env.example...
    copy .env.example .env
    echo Please edit .env and set your MySQL password
    pause
)

REM Set default values if not set
if not defined DB_USERNAME set DB_USERNAME=root
if not defined DB_PASSWORD set DB_PASSWORD=

REM Check if database exists
echo Checking database...
mysql -u %DB_USERNAME% -p%DB_PASSWORD% -e "USE ecommerce_db" 2>nul
if %errorlevel% neq 0 (
    echo Creating database 'ecommerce_db'...
    mysql -u %DB_USERNAME% -p%DB_PASSWORD% -e "CREATE DATABASE ecommerce_db;"
    if %errorlevel% equ 0 (
        echo [OK] Database created
    ) else (
        echo [ERROR] Failed to create database
        echo Please create it manually: mysql -u root -p -e "CREATE DATABASE ecommerce_db;"
        pause
        exit /b 1
    )
) else (
    echo [OK] Database 'ecommerce_db' exists
)
echo.

REM Install frontend dependencies if needed
if not exist node_modules (
    echo Installing frontend dependencies...
    call npm install
    echo [OK] Frontend dependencies installed
    echo.
)

REM Start backend
echo Starting Backend (Spring Boot)...
cd backend
if exist target\admin-backend-1.0.0.jar (
    start "Backend Server" java -jar target\admin-backend-1.0.0.jar
    echo [OK] Backend started
) else (
    echo JAR file not found. Building with Maven...
    call mvn clean install -DskipTests
    if %errorlevel% equ 0 (
        start "Backend Server" java -jar target\admin-backend-1.0.0.jar
        echo [OK] Backend started
    ) else (
        echo [ERROR] Failed to build backend
        pause
        exit /b 1
    )
)
cd ..
echo.

REM Wait for backend to start
echo Waiting for backend to be ready...
timeout /t 10 /nobreak >nul
echo [OK] Backend should be ready
echo.

REM Create admin user if it doesn't exist
echo Checking admin user...
mysql -u %DB_USERNAME% -p%DB_PASSWORD% ecommerce_db -e "INSERT IGNORE INTO users (email, password, full_name, mobile, role, created_at, updated_at, is_active, two_factor_enabled) VALUES ('admin@example.com', '$2a$10$vU5Hy8HlQWlOAhbHOEpz5.0QN7OyLG5xMjBVXH5xBx5bZCKGQ8vEm', 'Admin User', '1234567890', 'ADMIN', NOW(), NOW(), 1, 0);" 2>nul
echo [OK] Admin user ready
echo.

REM Start frontend
echo Starting Frontend (Vite)...
start "Frontend Server" npm run dev
echo [OK] Frontend started
echo.

echo ========================================
echo Application is running!
echo.
echo Frontend: http://localhost:5000
echo Backend:  http://localhost:8080/api/v1
echo Swagger:  http://localhost:8080/api/v1/swagger-ui.html
echo.
echo Admin Login:
echo   Email:    admin@example.com
echo   Password: admin123
echo.
echo Press any key to stop all services
echo ========================================
pause >nul

REM Stop services
taskkill /FI "WindowTitle eq Backend Server*" /F >nul 2>&1
taskkill /FI "WindowTitle eq Frontend Server*" /F >nul 2>&1
echo Services stopped.
