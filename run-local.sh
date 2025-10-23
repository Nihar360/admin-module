#!/bin/bash

# Local Development Startup Script
# This script helps you run the e-commerce admin dashboard locally

echo "🚀 E-Commerce Admin Dashboard - Local Setup"
echo "==========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if MySQL is running
echo -e "${YELLOW}Checking MySQL...${NC}"
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}❌ MySQL is not installed${NC}"
    echo "Please install MySQL from: https://dev.mysql.com/downloads/mysql/"
    exit 1
fi

if ! mysqladmin ping -h localhost --silent; then
    echo -e "${RED}❌ MySQL is not running${NC}"
    echo "Please start MySQL service:"
    echo "  - Linux: sudo systemctl start mysql"
    echo "  - Mac: brew services start mysql"
    echo "  - Windows: Start MySQL service from Services"
    exit 1
fi
echo -e "${GREEN}✓ MySQL is running${NC}"
echo ""

# Check if Java is installed
echo -e "${YELLOW}Checking Java...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java is not installed${NC}"
    echo "Please install Java 19+ from: https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi
echo -e "${GREEN}✓ Java is installed${NC}"
echo ""

# Check if Node.js is installed
echo -e "${YELLOW}Checking Node.js...${NC}"
if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Node.js is not installed${NC}"
    echo "Please install Node.js from: https://nodejs.org/"
    exit 1
fi
echo -e "${GREEN}✓ Node.js is installed${NC}"
echo ""

# Check for .env file
if [ ! -f ".env" ]; then
    echo -e "${YELLOW}⚠️  No .env file found${NC}"
    echo "Creating .env from .env.example..."
    cp .env.example .env
    echo -e "${YELLOW}⚠️  Please edit .env and set your MySQL password${NC}"
    echo ""
    read -p "Press Enter to continue after editing .env, or Ctrl+C to exit..."
fi

# Load environment variables
if [ -f ".env" ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# Check if database exists
echo -e "${YELLOW}Checking database...${NC}"
if mysql -u ${DB_USERNAME:-root} -p${DB_PASSWORD} -e "USE ecommerce_db" 2>/dev/null; then
    echo -e "${GREEN}✓ Database 'ecommerce_db' exists${NC}"
else
    echo -e "${YELLOW}Creating database 'ecommerce_db'...${NC}"
    mysql -u ${DB_USERNAME:-root} -p${DB_PASSWORD} -e "CREATE DATABASE ecommerce_db;" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Database created${NC}"
    else
        echo -e "${RED}❌ Failed to create database${NC}"
        echo "Please create it manually: mysql -u root -p -e 'CREATE DATABASE ecommerce_db;'"
        exit 1
    fi
fi
echo ""

# Check if admin user exists
echo -e "${YELLOW}Checking admin user...${NC}"
ADMIN_EXISTS=$(mysql -u ${DB_USERNAME:-root} -p${DB_PASSWORD} ecommerce_db -se "SELECT COUNT(*) FROM users WHERE email='admin@example.com'" 2>/dev/null)
if [ "$ADMIN_EXISTS" = "1" ]; then
    echo -e "${GREEN}✓ Admin user exists${NC}"
else
    echo -e "${YELLOW}Admin user not found. Will be created after backend starts...${NC}"
fi
echo ""

# Install frontend dependencies if needed
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}Installing frontend dependencies...${NC}"
    npm install
    echo -e "${GREEN}✓ Frontend dependencies installed${NC}"
    echo ""
fi

# Start backend
echo -e "${YELLOW}Starting Backend (Spring Boot)...${NC}"
cd backend
if [ -f "target/admin-backend-1.0.0.jar" ]; then
    java -jar target/admin-backend-1.0.0.jar &
    BACKEND_PID=$!
    echo -e "${GREEN}✓ Backend started (PID: $BACKEND_PID)${NC}"
else
    echo -e "${YELLOW}JAR file not found. Building with Maven...${NC}"
    mvn clean install -DskipTests
    if [ $? -eq 0 ]; then
        java -jar target/admin-backend-1.0.0.jar &
        BACKEND_PID=$!
        echo -e "${GREEN}✓ Backend started (PID: $BACKEND_PID)${NC}"
    else
        echo -e "${RED}❌ Failed to build backend${NC}"
        exit 1
    fi
fi
cd ..
echo ""

# Wait for backend to start
echo -e "${YELLOW}Waiting for backend to be ready...${NC}"
for i in {1..30}; do
    if curl -s http://localhost:8080/api/v1/actuator/health > /dev/null; then
        echo -e "${GREEN}✓ Backend is ready${NC}"
        break
    fi
    sleep 2
    echo -n "."
done
echo ""

# Create admin user if it doesn't exist
if [ "$ADMIN_EXISTS" != "1" ]; then
    echo -e "${YELLOW}Creating admin user...${NC}"
    mysql -u ${DB_USERNAME:-root} -p${DB_PASSWORD} ecommerce_db << 'EOF'
INSERT IGNORE INTO users (email, password, full_name, mobile, role, created_at, updated_at, is_active, two_factor_enabled)
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
EOF
    echo -e "${GREEN}✓ Admin user created${NC}"
    echo ""
fi

# Start frontend
echo -e "${YELLOW}Starting Frontend (Vite)...${NC}"
npm run dev &
FRONTEND_PID=$!
echo -e "${GREEN}✓ Frontend started (PID: $FRONTEND_PID)${NC}"
echo ""

echo "==========================================="
echo -e "${GREEN}🎉 Application is running!${NC}"
echo ""
echo "Frontend: http://localhost:5000"
echo "Backend:  http://localhost:8080/api/v1"
echo "Swagger:  http://localhost:8080/api/v1/swagger-ui.html"
echo ""
echo "Admin Login:"
echo "  Email:    admin@example.com"
echo "  Password: admin123"
echo ""
echo "Press Ctrl+C to stop all services"
echo "==========================================="

# Wait for Ctrl+C
trap "echo ''; echo 'Stopping services...'; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit 0" INT
wait
