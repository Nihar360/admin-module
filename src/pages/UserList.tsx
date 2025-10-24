import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AdminLayout } from '../components/admin/layout/AdminLayout';
import { DataTable } from '../components/admin/shared/DataTable';
import { FilterPanel } from '../components/admin/shared/FilterPanel';
import { adminApi, User } from '../api/adminApi';
import { Eye, MoreVertical } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '../components/ui/dropdown-menu';
import { toast } from 'sonner';

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

  const handleToggleUserStatus = async (userId: number, currentStatus: boolean) => {
    try {
      await adminApi.updateUser(userId, { isActive: !currentStatus });
      toast.success(`User ${!currentStatus ? 'activated' : 'deactivated'} successfully`);
      loadUsers();
    } catch (error) {
      toast.error('Failed to update user status');
    }
  };

  const columns = [
    {
      key: 'fullName',
      header: 'Customer',
      render: (user: User) => (
        <div>
          <div>{user.fullName}</div>
          <div className="text-sm text-gray-500">{user.email}</div>
        </div>
      ),
    },
    {
      key: 'mobile',
      header: 'Phone',
      render: (user: User) => user.mobile || 'N/A',
    },
    {
      key: 'role',
      header: 'Role',
      render: (user: User) => (
        <Badge variant="outline">{user.role}</Badge>
      ),
    },
    {
      key: 'createdAt',
      header: 'Joined',
      render: (user: User) => new Date(user.createdAt).toLocaleDateString(),
    },
    {
      key: 'isActive',
      header: 'Status',
      render: (user: User) => (
        <Badge variant={user.isActive ? 'default' : 'destructive'}>
          {user.isActive ? 'Active' : 'Blocked'}
        </Badge>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (user: User) => (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button size="sm" variant="ghost" onClick={(e) => e.stopPropagation()}>
              <MoreVertical className="w-4 h-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem
              onClick={(e) => {
                e.stopPropagation();
                navigate(`/admin/users/${user.id}`);
              }}
            >
              <Eye className="w-4 h-4 mr-2" />
              View Details
            </DropdownMenuItem>
            <DropdownMenuItem
              onClick={(e) => {
                e.stopPropagation();
                handleToggleUserStatus(user.id, user.isActive);
              }}
            >
              {user.isActive ? 'Deactivate User' : 'Activate User'}
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
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