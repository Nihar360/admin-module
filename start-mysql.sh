#!/bin/bash

# Set up MariaDB directories
export MYSQL_HOME="$PWD/mysql"
export MYSQL_DATADIR="$MYSQL_HOME/data"
export MYSQL_UNIX_PORT="$MYSQL_HOME/mysql.sock"
export MYSQL_PID_FILE="$MYSQL_HOME/mysql.pid"

# Create directories if they don't exist
mkdir -p "$MYSQL_HOME"

# Initialize database if not exists
if [ ! -d "$MYSQL_DATADIR" ]; then
  echo "Initializing MariaDB..."
  mysql_install_db --no-defaults --auth-root-authentication-method=normal \
    --datadir="$MYSQL_DATADIR" \
    --pid-file="$MYSQL_PID_FILE"
fi

# Start MariaDB server
echo "Starting MariaDB on socket..."
mysqld --no-defaults \
  --datadir="$MYSQL_DATADIR" \
  --pid-file="$MYSQL_PID_FILE" \
  --socket="$MYSQL_UNIX_PORT" \
  --bind-address=127.0.0.1 \
  --port=3306 \
  2>&1 | tee "$MYSQL_HOME/mysql.log" &

# Wait for MySQL to start
echo "Waiting for MariaDB to start..."
sleep 5

# Check if it's running
if mysql -u root --socket="$MYSQL_UNIX_PORT" -e "SELECT 1;" > /dev/null 2>&1; then
  echo "MariaDB started successfully!"
  
  # Create database and admin user
  echo "Creating ecommerce_db database and users..."
  mysql -u root --socket="$MYSQL_UNIX_PORT" <<EOF
-- Create database
CREATE DATABASE IF NOT EXISTS ecommerce_db;

-- Set root password
ALTER USER 'root'@'localhost' IDENTIFIED BY '${DATABASE_PASSWORD}';

-- Create database user for application
CREATE USER IF NOT EXISTS '${DATABASE_USERNAME}'@'localhost' IDENTIFIED BY '${DATABASE_PASSWORD}';
CREATE USER IF NOT EXISTS '${DATABASE_USERNAME}'@'127.0.0.1' IDENTIFIED BY '${DATABASE_PASSWORD}';
GRANT ALL PRIVILEGES ON ecommerce_db.* TO '${DATABASE_USERNAME}'@'localhost';
GRANT ALL PRIVILEGES ON ecommerce_db.* TO '${DATABASE_USERNAME}'@'127.0.0.1';
FLUSH PRIVILEGES;

-- Show databases
SHOW DATABASES;
EOF
  
  echo "Database setup complete!"
  echo "Root password has been set."
  echo "Database: ecommerce_db"
  echo "User: ${DATABASE_USERNAME}"
else
  echo "Failed to start MariaDB. Check logs at $MYSQL_HOME/mysql.log"
  exit 1
fi

# Keep the script running
wait
