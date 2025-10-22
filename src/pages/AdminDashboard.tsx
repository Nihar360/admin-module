import React, { useEffect, useState } from 'react';
import { AdminLayout } from '../components/admin/layout/AdminLayout';
import { StatCard } from '../components/admin/dashboard/StatCard';
import { RecentOrders } from '../components/admin/dashboard/RecentOrders';
import { useAdminDashboard } from '../hooks/useAdminDashboard';
import { useAdminOrders } from '../hooks/useAdminOrders';
import { ShoppingCart, Users } from 'lucide-react';
import { Skeleton } from '../components/ui/skeleton';

export const AdminDashboard: React.FC = () => {
  const { stats, isLoading } = useAdminDashboard();
  const { orders } = useAdminOrders();

  if (isLoading) {
    return (
      <AdminLayout>
        <div className="space-y-6">
          <h1 className="text-3xl">Dashboard</h1>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {[...Array(2)].map((_, i) => (
              <Skeleton key={i} className="h-32" />
            ))}
          </div>
        </div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout>
      <div className="space-y-6">
        <h1 className="text-3xl">Dashboard</h1>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <StatCard
            title="Total Orders"
            value={stats?.totalOrders.toLocaleString() || 0}
            change={stats?.ordersChange}
            icon={<ShoppingCart className="w-5 h-5" />}
          />
          <StatCard
            title="Total Customers"
            value={stats?.totalCustomers.toLocaleString() || 0}
            change={stats?.customersChange}
            icon={<Users className="w-5 h-5" />}
          />
        </div>

        <div>
          <RecentOrders orders={orders} />
        </div>
      </div>
    </AdminLayout>
  );
};
