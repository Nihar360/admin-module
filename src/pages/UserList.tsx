import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AdminLayout } from '../components/admin/layout/AdminLayout';
import { DataTable } from '../components/admin/shared/DataTable';
import { FilterPanel } from '../components/admin/shared/FilterPanel';
import { adminApi, User } from '../api/adminApi';
import { Eye } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';

export const UserList: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [filters, setFilters] = useState({
    status: 'all',
    search: '',
  });
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadUsers();
  }, [filters]);

  const loadUsers = async () => {
    try {
      const data = await adminApi.getUsers(filters);
      setUsers(data);
    } catch (error) {
      console.error('Failed to load users');
    } finally {
      setIsLoading(false);
    }
  };

  const handleFilterChange = (name: string, value: string) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleReset = () => {
    setFilters({ status: 'all', search: '' });
  };

  const columns = [
    {
      key: 'name',
      header: 'Customer',
      render: (user: User) => (
        <div>
          <div>{user.name}</div>
          <div className="text-sm text-gray-500">{user.email}</div>
        </div>
      ),
    },
    {
      key: 'phone',
      header: 'Phone',
    },
    {
      key: 'totalOrders',
      header: 'Orders',
    },
    {
      key: 'totalSpent',
      header: 'Total Spent',
      render: (user: User) => `$${user.totalSpent.toFixed(2)}`,
    },
    {
      key: 'joinedAt',
      header: 'Joined',
      render: (user: User) => new Date(user.joinedAt).toLocaleDateString(),
    },
    {
      key: 'status',
      header: 'Status',
      render: (user: User) => (
        <Badge variant={user.status === 'active' ? 'default' : 'destructive'}>
          {user.status}
        </Badge>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (user: User) => (
        <Button
          size="sm"
          variant="ghost"
          onClick={(e) => {
            e.stopPropagation();
            navigate(`/admin/users/${user.id}`);
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
      placeholder: 'Search customers...',
    },
    {
      type: 'select' as const,
      name: 'status',
      label: 'Status',
      placeholder: 'All Statuses',
      options: [
        { value: 'all', label: 'All Statuses' },
        { value: 'active', label: 'Active' },
        { value: 'blocked', label: 'Blocked' },
      ],
    },
  ];

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl">Customers</h1>
        </div>

        <FilterPanel
          filters={filterConfig}
          values={filters}
          onChange={handleFilterChange}
          onReset={handleReset}
        />

        {isLoading ? (
          <div className="text-center py-8">Loading customers...</div>
        ) : (
          <DataTable
            data={users}
            columns={columns}
            onRowClick={(user) => navigate(`/admin/users/${user.id}`)}
          />
        )}
      </div>
    </AdminLayout>
  );
};
