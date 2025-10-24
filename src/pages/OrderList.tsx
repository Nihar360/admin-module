import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AdminLayout } from '../components/admin/layout/AdminLayout';
import { DataTable } from '../components/admin/shared/DataTable';
import { FilterPanel } from '../components/admin/shared/FilterPanel';
import { OrderStatusBadge } from '../components/admin/orders/OrderStatusBadge';
import { useAdminOrders } from '../hooks/useAdminOrders';
import { Order, adminApi } from '../api/adminApi';
import { Eye, MoreVertical } from 'lucide-react';
import { Button } from '../components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '../components/ui/dropdown-menu';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from '../components/ui/dialog';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { toast } from 'sonner';

export const OrderList: React.FC = () => {
  const [filters, setFilters] = useState({
    status: 'all',
    search: '',
  });
  const { orders, isLoading, reload, updateOrderStatus } = useAdminOrders(filters);
  const navigate = useNavigate();
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [showStatusDialog, setShowStatusDialog] = useState(false);
  const [showRefundDialog, setShowRefundDialog] = useState(false);
  const [newStatus, setNewStatus] = useState('');
  const [refundAmount, setRefundAmount] = useState('');
  const [refundReason, setRefundReason] = useState('');

  const handleFilterChange = (name: string, value: string) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleReset = () => {
    setFilters({ status: 'all', search: '' });
  };

  const handleStatusUpdate = async () => {
    if (!selectedOrder || !newStatus) return;
    try {
      await updateOrderStatus(selectedOrder.id, newStatus);
      toast.success('Order status updated successfully');
      setShowStatusDialog(false);
      setNewStatus('');
      setSelectedOrder(null);
      reload();
    } catch (error) {
      toast.error('Failed to update order status');
    }
  };

  const handleRefund = async () => {
    if (!selectedOrder || !refundAmount || !refundReason) return;
    try {
      await adminApi.processRefund(selectedOrder.id, parseFloat(refundAmount), refundReason);
      toast.success('Refund processed successfully');
      setShowRefundDialog(false);
      setRefundAmount('');
      setRefundReason('');
      setSelectedOrder(null);
      reload();
    } catch (error) {
      toast.error('Failed to process refund');
    }
  };

  const columns = [
    {
      key: 'orderNumber',
      header: 'Order Number',
    },
    {
      key: 'userId',
      header: 'User ID',
      render: (order: Order) => `#${order.userId}`,
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
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button 
              size="sm" 
              variant="ghost" 
              onClick={(e: React.MouseEvent) => e.stopPropagation()}
            >
              <MoreVertical className="w-4 h-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem
              onSelect={() => {
                navigate(`/admin/orders/${order.id}`);
              }}
            >
              <Eye className="w-4 h-4 mr-2" />
              View Details
            </DropdownMenuItem>
            <DropdownMenuItem
              onSelect={() => {
                setSelectedOrder(order);
                setNewStatus(order.status);
                setShowStatusDialog(true);
              }}
            >
              Update Status
            </DropdownMenuItem>
            <DropdownMenuItem
              onSelect={() => {
                setSelectedOrder(order);
                setRefundAmount(order.total.toString());
                setShowRefundDialog(true);
              }}
            >
              Process Refund
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
      placeholder: 'Search orders...',
    },
    {
      type: 'select' as const,
      name: 'status',
      label: 'Status',
      placeholder: 'All Statuses',
      options: [
        { value: 'all', label: 'All Statuses' },
        { value: 'PENDING', label: 'Pending' },
        { value: 'PROCESSING', label: 'Processing' },
        { value: 'SHIPPED', label: 'Shipped' },
        { value: 'DELIVERED', label: 'Delivered' },
        { value: 'CANCELLED', label: 'Cancelled' },
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

      <Dialog open={showStatusDialog} onOpenChange={setShowStatusDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Update Order Status</DialogTitle>
            <DialogDescription>
              Change the status of Order #{selectedOrder?.orderNumber}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <Label htmlFor="status">New Status</Label>
              <select
                id="status"
                className="w-full mt-1 p-2 border rounded-md"
                value={newStatus}
                onChange={(e) => setNewStatus(e.target.value)}
              >
                <option value="PENDING">Pending</option>
                <option value="PROCESSING">Processing</option>
                <option value="SHIPPED">Shipped</option>
                <option value="DELIVERED">Delivered</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowStatusDialog(false)}>
              Cancel
            </Button>
            <Button onClick={handleStatusUpdate}>Update Status</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={showRefundDialog} onOpenChange={setShowRefundDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Process Refund</DialogTitle>
            <DialogDescription>
              Process a refund for Order #{selectedOrder?.orderNumber}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <Label htmlFor="refundAmount">Refund Amount</Label>
              <Input
                id="refundAmount"
                type="number"
                step="0.01"
                value={refundAmount}
                onChange={(e) => setRefundAmount(e.target.value)}
                placeholder="Enter refund amount"
              />
            </div>
            <div>
              <Label htmlFor="refundReason">Reason</Label>
              <Input
                id="refundReason"
                value={refundReason}
                onChange={(e) => setRefundReason(e.target.value)}
                placeholder="Enter refund reason"
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowRefundDialog(false)}>
              Cancel
            </Button>
            <Button onClick={handleRefund}>Process Refund</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </AdminLayout>
  );
};