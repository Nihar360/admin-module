import { useState, useEffect } from 'react';
import { adminApi, Order } from '../api/adminApi';

export const useAdminOrders = (filters?: {
  status?: string;
  search?: string;
}) => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [total, setTotal] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadOrders();
  }, [filters?.status, filters?.search]);

  const loadOrders = async () => {
    try {
      setIsLoading(true);
      const data = await adminApi.getOrders(filters);
      setOrders(data.orders);
      setTotal(data.total);
      setError(null);
    } catch (err) {
      setError('Failed to load orders');
    } finally {
      setIsLoading(false);
    }
  };

  const updateOrderStatus = async (id: string, status: Order['status']) => {
    try {
      await adminApi.updateOrderStatus(id, status);
      await loadOrders();
    } catch (err) {
      throw new Error('Failed to update order status');
    }
  };

  return { orders, total, isLoading, error, reload: loadOrders, updateOrderStatus };
};
