import { useState, useEffect } from 'react';
import { adminApi, DashboardStats, SalesData } from '../api/adminApi';

export const useAdminDashboard = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [salesData, setSalesData] = useState<SalesData[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setIsLoading(true);
      const [statsData, sales] = await Promise.all([
        adminApi.getDashboardStats(),
        adminApi.getSalesData(30),
      ]);
      setStats(statsData);
      setSalesData(sales);
      setError(null);
    } catch (err) {
      setError('Failed to load dashboard data');
    } finally {
      setIsLoading(false);
    }
  };

  return { stats, salesData, isLoading, error, reload: loadDashboardData };
};
