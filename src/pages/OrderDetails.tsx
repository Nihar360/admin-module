import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { AdminLayout } from '../components/admin/layout/AdminLayout';
import { OrderStatusBadge } from '../components/admin/orders/OrderStatusBadge';
import { OrderTimeline } from '../components/admin/orders/OrderTimeline';
import { RefundModal } from '../components/admin/orders/RefundModal';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { adminApi, Order } from '../api/adminApi';
import { ArrowLeft, Truck, Ban, RotateCcw } from 'lucide-react';
import { Badge } from '../components/ui/badge';
import { Separator } from '../components/ui/separator';
import { toast } from 'sonner@2.0.3';

export const OrderDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [order, setOrder] = useState<Order | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [showRefundModal, setShowRefundModal] = useState(false);

  useEffect(() => {
    loadOrder();
  }, [id]);

  const loadOrder = async () => {
    if (!id) return;
    try {
      const data = await adminApi.getOrder(id);
      setOrder(data);
    } catch (error) {
      toast('Failed to load order');
    } finally {
      setIsLoading(false);
    }
  };

  const handleStatusUpdate = async (status: Order['status']) => {
    if (!id) return;
    try {
      await adminApi.updateOrderStatus(id, status);
      await loadOrder();
      toast('Order status updated successfully');
    } catch (error) {
      toast('Failed to update order status');
    }
  };

  const handleRefund = async (amount: number, reason: string) => {
    toast(`Refund of $${amount.toFixed(2)} processed. Reason: ${reason}`);
  };

  if (isLoading) {
    return (
      <AdminLayout>
        <div className="text-center py-8">Loading order...</div>
      </AdminLayout>
    );
  }

  if (!order) {
    return (
      <AdminLayout>
        <div className="text-center py-8">Order not found</div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="icon" onClick={() => navigate('/admin/orders')}>
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <div className="flex-1">
            <h1 className="text-3xl">{order.orderNumber}</h1>
            <p className="text-gray-500">
              Placed on {new Date(order.createdAt).toLocaleString()}
            </p>
          </div>
          <OrderStatusBadge status={order.status} />
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Order Items</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {order.items.map((item) => (
                    <div key={item.id} className="flex items-center gap-4 pb-4 border-b last:border-0">
                      <div className="w-16 h-16 bg-gray-100 rounded flex items-center justify-center">
                        <span className="text-gray-400">No image</span>
                      </div>
                      <div className="flex-1">
                        <div>{item.name}</div>
                        <div className="text-sm text-gray-500">Qty: {item.quantity}</div>
                      </div>
                      <div>${(item.price * item.quantity).toFixed(2)}</div>
                    </div>
                  ))}
                  <Separator />
                  <div className="flex justify-between">
                    <span>Total</span>
                    <span className="text-xl">${order.total.toFixed(2)}</span>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Customer Information</CardTitle>
              </CardHeader>
              <CardContent className="space-y-2">
                <div>
                  <div className="text-sm text-gray-500">Name</div>
                  <div>{order.customer.name}</div>
                </div>
                <div>
                  <div className="text-sm text-gray-500">Email</div>
                  <div>{order.customer.email}</div>
                </div>
                <div>
                  <div className="text-sm text-gray-500">Phone</div>
                  <div>{order.customer.phone}</div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Shipping Address</CardTitle>
              </CardHeader>
              <CardContent>
                <div>
                  {order.shippingAddress.street}
                  <br />
                  {order.shippingAddress.city}, {order.shippingAddress.state} {order.shippingAddress.zipCode}
                  <br />
                  {order.shippingAddress.country}
                </div>
              </CardContent>
            </Card>
          </div>

          <div className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Order Status</CardTitle>
              </CardHeader>
              <CardContent>
                <OrderTimeline order={order} />
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Actions</CardTitle>
              </CardHeader>
              <CardContent className="space-y-2">
                {order.status === 'pending' && (
                  <Button
                    className="w-full"
                    onClick={() => handleStatusUpdate('processing')}
                  >
                    <Truck className="w-4 h-4 mr-2" />
                    Mark as Processing
                  </Button>
                )}
                {order.status === 'processing' && (
                  <Button
                    className="w-full"
                    onClick={() => handleStatusUpdate('shipped')}
                  >
                    <Truck className="w-4 h-4 mr-2" />
                    Mark as Shipped
                  </Button>
                )}
                {order.status === 'shipped' && (
                  <Button
                    className="w-full"
                    onClick={() => handleStatusUpdate('delivered')}
                  >
                    <Truck className="w-4 h-4 mr-2" />
                    Mark as Delivered
                  </Button>
                )}
                {order.status !== 'cancelled' && order.status !== 'delivered' && (
                  <Button
                    className="w-full"
                    variant="destructive"
                    onClick={() => handleStatusUpdate('cancelled')}
                  >
                    <Ban className="w-4 h-4 mr-2" />
                    Cancel Order
                  </Button>
                )}
                {order.paymentStatus === 'paid' && (
                  <Button
                    className="w-full"
                    variant="outline"
                    onClick={() => setShowRefundModal(true)}
                  >
                    <RotateCcw className="w-4 h-4 mr-2" />
                    Process Refund
                  </Button>
                )}
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Payment</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <div className="flex justify-between">
                    <span>Status</span>
                    <Badge>{order.paymentStatus}</Badge>
                  </div>
                  <div className="flex justify-between">
                    <span>Total</span>
                    <span>${order.total.toFixed(2)}</span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      <RefundModal
        open={showRefundModal}
        onOpenChange={setShowRefundModal}
        orderTotal={order.total}
        onConfirm={handleRefund}
      />
    </AdminLayout>
  );
};
