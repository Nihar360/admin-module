# Admin Dashboard Project

## Overview
This is a React-based admin dashboard application built with modern web technologies. It provides a comprehensive interface for managing orders, products, users, inventory, and reports.

## Tech Stack
- **Frontend Framework**: React 18.3.1
- **Build Tool**: Vite 6.3.5
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **UI Components**: Radix UI primitives
- **Routing**: React Router DOM
- **Charts**: Recharts
- **Form Management**: React Hook Form
- **Icons**: Lucide React

## Project Structure
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

### Running the App
The app runs on port 5000 and is configured for the Replit environment with:
- Host: 0.0.0.0 (accessible from anywhere)
- HMR configured for Replit's proxy setup
- WebSocket support for hot module replacement

### Environment Configuration
- Development server runs on port 5000
- Build output: `build/` directory
- Uses React SWC plugin for fast refresh

## Recent Changes (October 22, 2025)
- Imported project from GitHub
- Added TypeScript configuration (tsconfig.json)
- Configured Vite for Replit environment (port 5000, host 0.0.0.0)
- Set up workflow for development server
- Added .gitignore for Node.js projects
- Created project documentation

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
