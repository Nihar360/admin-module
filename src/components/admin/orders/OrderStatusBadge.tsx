import { Badge } from '@/components/ui/badge';

interface OrderStatusBadgeProps {
  status: string;
}

const statusConfig: Record<string, { variant: 'default' | 'secondary' | 'destructive' | 'outline'; label: string; className?: string }> = {
  PENDING: {
    variant: 'outline',
    label: 'Pending',
    className: 'bg-yellow-50 text-yellow-700 border-yellow-300',
  },
  CONFIRMED: {
    variant: 'default',
    label: 'Confirmed',
    className: 'bg-blue-50 text-blue-700 border-blue-300',
  },
  PROCESSING: {
    variant: 'default',
    label: 'Processing',
    className: 'bg-indigo-50 text-indigo-700 border-indigo-300',
  },
  SHIPPED: {
    variant: 'default',
    label: 'Shipped',
    className: 'bg-purple-50 text-purple-700 border-purple-300',
  },
  DELIVERED: {
    variant: 'default',
    label: 'Delivered',
    className: 'bg-green-50 text-green-700 border-green-300',
  },
  CANCELLED: {
    variant: 'destructive',
    label: 'Cancelled',
    className: 'bg-red-50 text-red-700 border-red-300',
  },
  REFUNDED: {
    variant: 'secondary',
    label: 'Refunded',
    className: 'bg-gray-50 text-gray-700 border-gray-300',
  },
};

export const OrderStatusBadge = ({ status }: OrderStatusBadgeProps) => {
  // Handle undefined or null status
  if (!status) {
    console.warn('OrderStatusBadge: Received undefined/null status');
    return (
      <Badge variant="outline" className="bg-gray-50 text-gray-500">
        Unknown
      </Badge>
    );
  }

  // Normalize status to uppercase
  const normalizedStatus = status.toString().toUpperCase().trim();
  
  console.log('OrderStatusBadge: Rendering status:', { original: status, normalized: normalizedStatus });
  
  // Get config or use default
  const config = statusConfig[normalizedStatus] || {
    variant: 'outline' as const,
    label: status,
    className: 'bg-gray-50 text-gray-700 border-gray-300',
  };

  return (
    <Badge variant={config.variant} className={config.className}>
      {config.label}
    </Badge>
  );
};