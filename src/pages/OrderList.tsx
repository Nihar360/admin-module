import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AdminLayout } from '../components/admin/layout/AdminLayout';
import { DataTable } from '../components/admin/shared/DataTable';
import { FilterPanel } from '../components/admin/shared/FilterPanel';
import { OrderStatusBadge } from '../components/admin/orders/OrderStatusBadge';
import { useAdminOrders } from '../hooks/useAdminOrders';
import { Order } from '../api/adminApi';
import { Eye } from 'lucide-react';
import { Button } from '../components/ui/button';

export const OrderList: React.FC = () => {
  const [filters, setFilters] = useState({
    status: 'all',
    search: '',
  });
  const { orders, isLoading } = useAdminOrders(filters);
  const navigate = useNavigate();

  const handleFilterChange = (name: string, value: string) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleReset = () => {
    setFilters({ status: 'all', search: '' });
  };

  const columns = [
    {
      key: 'orderNumber',
      header: 'Order Number',
    },
    {
      key: 'customer',
      header: 'Customer',
      render: (order: Order) => (
        <div>
          <div>{order.customer.name}</div>
          <div className="text-sm text-gray-500">{order.customer.email}</div>
        </div>
      ),
    },
    {
      key: 'total',
      header: 'Total',
      render: (order: Order) => `$${order.total.toFixed(2)}`,
    },
    {
      key: 'status',
      header: 'Status',
      render: (order: Order) => <OrderStatusBadge status={order.status} />,
    },
    {
      key: 'createdAt',
      header: 'Date',
      render: (order: Order) => new Date(order.createdAt).toLocaleDateString(),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (order: Order) => (
        <Button
          size="sm"
          variant="ghost"
          onClick={(e) => {
            e.stopPropagation();
            navigate(`/admin/orders/${order.id}`);
          }}
        >
          <Eye className="w-4 h-4 mr-2" />
          View
        </Button>
      ),
    },
  ];

  const filterConfig = [
    {
      type: 'text' as const,
      name: 'search',
      label: 'Search',
      placeholder: 'Search orders...',
    },
    {
      type: 'select' as const,
      name: 'status',
      label: 'Status',
      placeholder: 'All Statuses',
      options: [
        { value: 'all', label: 'All Statuses' },
        { value: 'pending', label: 'Pending' },
        { value: 'processing', label: 'Processing' },
        { value: 'shipped', label: 'Shipped' },
        { value: 'delivered', label: 'Delivered' },
        { value: 'cancelled', label: 'Cancelled' },
      ],
    },
  ];

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl">Orders</h1>
        </div>

        <FilterPanel
          filters={filterConfig}
          values={filters}
          onChange={handleFilterChange}
          onReset={handleReset}
        />

        {isLoading ? (
          <div className="text-center py-8">Loading orders...</div>
        ) : (
          <DataTable
            data={orders}
            columns={columns}
            onRowClick={(order) => navigate(`/admin/orders/${order.id}`)}
          />
        )}
      </div>
    </AdminLayout>
  );
};
