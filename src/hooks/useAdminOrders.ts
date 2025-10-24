import { useState, useEffect, useCallback } from 'react';
import { adminApi, Order } from '../api/adminApi';

interface UseAdminOrdersFilters {
  status?: string;
  search?: string;
  page?: number;
  limit?: number;
}

export const useAdminOrders = (filters: UseAdminOrdersFilters) => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const fetchOrders = useCallback(async () => {
    setIsLoading(true);
    setError(null);

    try {
      console.log('🔍 Fetching orders with filters:', filters);

      const pageResponse = await adminApi.getOrders({
        status: filters.status && filters.status !== 'all' ? filters.status : undefined,
        search: filters.search || undefined,
        page: filters.page || 0,
        limit: filters.limit || 10,
      });

      console.log('📦 Page Response:', pageResponse);
      console.log('📋 Orders Content:', pageResponse.content);
      console.log('📊 Total Elements:', pageResponse.totalElements);

      // Ensure we always set an array
      const ordersData = pageResponse.content || [];
      
      console.log('✅ Setting orders state:', ordersData);
      console.log('✅ Number of orders:', ordersData.length);
      
      if (ordersData.length > 0) {
        console.log('✅ First order:', ordersData[0]);
        console.log('✅ First order status:', ordersData[0].status);
      }

      setOrders(ordersData);
      setTotalPages(pageResponse.totalPages || 0);
      setTotalElements(pageResponse.totalElements || 0);

    } catch (err: any) {
      console.error('❌ Error fetching orders:', err);
      console.error('❌ Error details:', err.response?.data);
      setError(err.message || 'Failed to fetch orders');
      setOrders([]); // Set empty array on error
    } finally {
      setIsLoading(false);
    }
  }, [filters.status, filters.search, filters.page, filters.limit]);

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  // Fixed: Accept both string and number for orderId
  const updateOrderStatus = async (orderId: string | number, status: string) => {
    try {
      console.log('🔄 Updating order status:', { orderId, status });
      // Convert to string if it's a number
      const orderIdStr = typeof orderId === 'number' ? orderId.toString() : orderId;
      await adminApi.updateOrderStatus(orderIdStr, status);
      console.log('✅ Order status updated successfully');
      await fetchOrders(); // Reload orders
    } catch (err: any) {
      console.error('❌ Error updating order status:', err);
      throw err;
    }
  };

  const reload = useCallback(() => {
    console.log('🔄 Reloading orders...');
    fetchOrders();
  }, [fetchOrders]);

  return {
    orders,
    isLoading,
    error,
    totalPages,
    totalElements,
    reload,
    updateOrderStatus,
  };
};