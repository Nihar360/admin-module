import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../../ui/card';
import { Badge } from '../../ui/badge';
import { Order } from '../../../api/adminApi';
import { OrderStatusBadge } from '../orders/OrderStatusBadge';

interface RecentOrdersProps {
  orders: Order[];
}

export const RecentOrders: React.FC<RecentOrdersProps> = ({ orders }) => {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Recent Orders</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {orders.slice(0, 5).map((order) => (
            <div key={order.id} className="flex items-center justify-between pb-4 border-b last:border-0">
              <div>
                <div>{order.orderNumber}</div>
                <div className="text-sm text-gray-500">{order.customer.name}</div>
              </div>
              <div className="text-right">
                <div className="mb-1">${order.total.toFixed(2)}</div>
                <OrderStatusBadge status={order.status} />
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};
