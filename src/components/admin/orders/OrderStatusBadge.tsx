import React from 'react';
import { Badge } from '../../ui/badge';
import { Order } from '../../../api/adminApi';

interface OrderStatusBadgeProps {
  status: Order['status'];
}

const statusConfig = {
  pending: { label: 'Pending', variant: 'secondary' as const },
  processing: { label: 'Processing', variant: 'default' as const },
  shipped: { label: 'Shipped', variant: 'default' as const },
  delivered: { label: 'Delivered', variant: 'default' as const },
  cancelled: { label: 'Cancelled', variant: 'destructive' as const },
};

export const OrderStatusBadge: React.FC<OrderStatusBadgeProps> = ({ status }) => {
  const config = statusConfig[status];

  return (
    <Badge variant={config.variant} className="text-xs">
      {config.label}
    </Badge>
  );
};
