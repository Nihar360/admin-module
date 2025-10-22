import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AdminAuthProvider } from './contexts/AdminAuthContext';
import { AdminAuthGuard } from './components/admin/layout/AdminAuthGuard';
import { AdminLogin } from './pages/AdminLogin';
import { AdminDashboard } from './pages/AdminDashboard';
import { NotificationsPage } from './pages/NotificationsPage';
import { OrderList } from './pages/OrderList';
import { OrderDetails } from './pages/OrderDetails';
import { ProductList } from './pages/ProductList';
import { ProductForm } from './pages/ProductForm';
import { CouponList } from './pages/CouponList';
import { UserList } from './pages/UserList';
import { InventoryManagement } from './pages/InventoryManagement';
import { SettingsPage } from './pages/SettingsPage';
import { ReportsPage } from './pages/ReportsPage';
import { Toaster } from './components/ui/sonner';

export default function App() {
  return (
    <BrowserRouter>
      <AdminAuthProvider>
        <Routes>
          <Route path="/" element={<Navigate to="/admin/login" replace />} />
          <Route path="/preview_page.html" element={<Navigate to="/admin/login" replace />} />
          <Route path="/admin/login" element={<AdminLogin />} />
          
          <Route
            path="/admin/dashboard"
            element={
              <AdminAuthGuard>
                <AdminDashboard />
              </AdminAuthGuard>
            }
          />

          <Route
            path="/admin/notifications"
            element={
              <AdminAuthGuard>
                <NotificationsPage />
              </AdminAuthGuard>
            }
          />
          
          <Route
            path="/admin/orders"
            element={
              <AdminAuthGuard>
                <OrderList />
              </AdminAuthGuard>
            }
          />
          
          <Route
            path="/admin/orders/:id"
            element={
              <AdminAuthGuard>
                <OrderDetails />
              </AdminAuthGuard>
            }
          />
          
          <Route
            path="/admin/products"
            element={
              <AdminAuthGuard>
                <ProductList />
              </AdminAuthGuard>
            }
          />
          
          <Route
            path="/admin/products/:id/edit"
            element={
              <AdminAuthGuard>
                <ProductForm />
              </AdminAuthGuard>
            }
          />
          
          <Route
            path="/admin/coupons"
            element={
              <AdminAuthGuard>
                <CouponList />
              </AdminAuthGuard>
            }
          />
          
          <Route
            path="/admin/users"
            element={
              <AdminAuthGuard>
                <UserList />
              </AdminAuthGuard>
            }
          />
          
          <Route
            path="/admin/inventory"
            element={
              <AdminAuthGuard>
                <InventoryManagement />
              </AdminAuthGuard>
            }
          />
          
          <Route
            path="/admin/settings"
            element={
              <AdminAuthGuard>
                <SettingsPage />
              </AdminAuthGuard>
            }
          />
          
          <Route
            path="/admin/reports"
            element={
              <AdminAuthGuard>
                <ReportsPage />
              </AdminAuthGuard>
            }
          />

          {/* Catch-all route for unmatched paths */}
          <Route path="*" element={<Navigate to="/admin/login" replace />} />
        </Routes>
        <Toaster />
      </AdminAuthProvider>
    </BrowserRouter>
  );
}
