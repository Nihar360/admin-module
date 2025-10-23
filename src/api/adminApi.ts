import apiClient from './apiClient';

// Types based on backend API responses
export interface Order {
  id: number;
  orderNumber: string;
  userId: number;
  status: string;
  paymentMethod: string;
  subtotal: number;
  discount: number;
  shipping: number;
  total: number;
  notes?: string;
  orderDate: string;
  deliveredDate?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Product {
  id: number;
  name: string;
  description?: string;
  price: number;
  originalPrice?: number;
  discountPrice?: number;
  categoryId: number;
  category?: string;
  stockCount: number;
  sku?: string;
  image: string;
  imageUrl?: string;
  images?: string[];
  badge?: string;
  rating: number;
  reviews: number;
  inStock: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Coupon {
  id: number;
  code: string;
  type: 'PERCENTAGE' | 'FIXED';
  value: number;
  minPurchase: number;
  maxDiscount?: number;
  usageLimit: number;
  usageCount: number;
  expiresAt: string;
  isActive: boolean;
}

export interface User {
  id: number;
  email: string;
  fullName: string;
  mobile?: string;
  role: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DashboardStats {
  totalRevenue: number;
  totalOrders: number;
  totalCustomers: number;
  averageOrderValue: number;
  revenueChange: number;
  ordersChange: number;
  customersChange: number;
  totalProducts?: number;
  lowStockProducts?: number;
  pendingOrders?: number;
}

export interface SalesData {
  date: string;
  revenue: number;
  orders: number;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  image?: string;
  itemCount?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;
}

// API functions
export const adminApi = {
  // Auth
  login: async (email: string, password: string) => {
    const response = await apiClient.post<ApiResponse<{ token: string; user: User }>>('/admin/auth/login', {
      email,
      password,
    });
    return response.data;
  },

  getCurrentUser: async () => {
    const response = await apiClient.get<ApiResponse<User>>('/admin/auth/me');
    return response.data;
  },

  changePassword: async (oldPassword: string, newPassword: string, confirmPassword?: string) => {
    const response = await apiClient.put<ApiResponse<void>>('/admin/auth/password', {
      oldPassword,
      newPassword,
      confirmPassword,
    });
    return response.data;
  },

  // Dashboard
  getDashboardStats: async (days: number = 30): Promise<DashboardStats> => {
    const response = await apiClient.get<ApiResponse<DashboardStats>>('/admin/dashboard/stats', {
      params: { days },
    });
    return response.data.data;
  },

  getSalesData: async (days: number = 30): Promise<SalesData[]> => {
    const response = await apiClient.get<ApiResponse<SalesData[]>>('/admin/dashboard/sales', {
      params: { days },
    });
    return response.data.data || [];
  },

  // Orders
  getOrders: async (filters?: {
    status?: string;
    search?: string;
    page?: number;
    limit?: number;
  }): Promise<{ orders: Order[]; total: number }> => {
    const response = await apiClient.get<ApiResponse<PageResponse<Order>>>('/admin/orders', {
      params: {
        status: filters?.status !== 'all' ? filters?.status : undefined,
        search: filters?.search,
        page: filters?.page || 0,
        size: filters?.limit || 10,
      },
    });
    return {
      orders: response.data.data.content,
      total: response.data.data.totalElements,
    };
  },

  getOrder: async (id: string | number): Promise<Order | null> => {
    try {
      const response = await apiClient.get<ApiResponse<Order>>(`/admin/orders/${id}`);
      return response.data.data;
    } catch (error) {
      console.error('Error fetching order:', error);
      return null;
    }
  },

  updateOrderStatus: async (id: string | number, status: string): Promise<Order> => {
    const response = await apiClient.put<ApiResponse<Order>>(`/admin/orders/${id}/status`, {
      status,
      notes: `Order status updated to ${status}`,
    });
    return response.data.data;
  },

  // Products
  getProducts: async (filters?: {
    category?: string;
    categoryId?: number;
    search?: string;
    inStock?: boolean;
    page?: number;
    size?: number;
  }): Promise<Product[]> => {
    const response = await apiClient.get<ApiResponse<PageResponse<Product>>>('/admin/products', {
      params: {
        categoryId: filters?.categoryId,
        search: filters?.search,
        inStock: filters?.inStock,
        page: filters?.page || 0,
        size: filters?.size || 50,
      },
    });
    return response.data.data.content;
  },

  getProduct: async (id: string | number): Promise<Product | null> => {
    try {
      const response = await apiClient.get<ApiResponse<Product>>(`/admin/products/${id}`);
      return response.data.data;
    } catch (error) {
      console.error('Error fetching product:', error);
      return null;
    }
  },

  createProduct: async (data: Partial<Product>): Promise<Product> => {
    const response = await apiClient.post<ApiResponse<Product>>('/admin/products', {
      name: data.name,
      description: data.description,
      price: data.price,
      originalPrice: data.originalPrice,
      discountPrice: data.discountPrice,
      categoryId: data.categoryId,
      stockCount: data.stockCount || 0,
      sku: data.sku,
      image: data.image || '',
      badge: data.badge,
      isActive: data.isActive !== false,
    });
    return response.data.data;
  },

  updateProduct: async (id: string | number, data: Partial<Product>): Promise<Product> => {
    const response = await apiClient.put<ApiResponse<Product>>(`/admin/products/${id}`, {
      name: data.name,
      description: data.description,
      price: data.price,
      originalPrice: data.originalPrice,
      discountPrice: data.discountPrice,
      categoryId: data.categoryId,
      stockCount: data.stockCount,
      sku: data.sku,
      image: data.image,
      badge: data.badge,
      isActive: data.isActive,
    });
    return response.data.data;
  },

  deleteProduct: async (id: string | number): Promise<void> => {
    await apiClient.delete(`/admin/products/${id}`);
  },

  adjustStock: async (id: string | number, quantity: number, type: 'ADD' | 'SUBTRACT'): Promise<Product> => {
    const response = await apiClient.put<ApiResponse<Product>>(`/admin/products/${id}/stock`, {
      quantity,
      type,
    });
    return response.data.data;
  },

  // Coupons
  getCoupons: async (): Promise<Coupon[]> => {
    const response = await apiClient.get<ApiResponse<Coupon[]>>('/admin/coupons');
    return response.data.data;
  },

  createCoupon: async (data: Partial<Coupon>): Promise<Coupon> => {
    const response = await apiClient.post<ApiResponse<Coupon>>('/admin/coupons', {
      code: data.code,
      type: data.type,
      value: data.value,
      minPurchase: data.minPurchase || 0,
      maxDiscount: data.maxDiscount,
      usageLimit: data.usageLimit || 100,
      expiresAt: data.expiresAt,
      isActive: data.isActive !== false,
    });
    return response.data.data;
  },

  updateCoupon: async (id: string | number, data: Partial<Coupon>): Promise<Coupon> => {
    const response = await apiClient.put<ApiResponse<Coupon>>(`/admin/coupons/${id}`, {
      code: data.code,
      type: data.type,
      value: data.value,
      minPurchase: data.minPurchase,
      maxDiscount: data.maxDiscount,
      usageLimit: data.usageLimit,
      expiresAt: data.expiresAt,
      isActive: data.isActive,
    });
    return response.data.data;
  },

  deleteCoupon: async (id: string | number): Promise<void> => {
    await apiClient.delete(`/admin/coupons/${id}`);
  },

  // Users
  getUsers: async (filters?: { search?: string; status?: string }): Promise<User[]> => {
    const response = await apiClient.get<ApiResponse<PageResponse<User>>>('/admin/users', {
      params: {
        search: filters?.search,
        isActive: filters?.status !== 'all' && filters?.status === 'active' ? true : filters?.status === 'blocked' ? false : undefined,
        page: 0,
        size: 100,
      },
    });
    return response.data.data.content;
  },

  getUser: async (id: string | number): Promise<User | null> => {
    try {
      const response = await apiClient.get<ApiResponse<User>>(`/admin/users/${id}`);
      return response.data.data;
    } catch (error) {
      console.error('Error fetching user:', error);
      return null;
    }
  },

  updateUser: async (id: string | number, data: Partial<User>): Promise<User> => {
    const response = await apiClient.put<ApiResponse<User>>(`/admin/users/${id}`, data);
    return response.data.data;
  },

  toggleUserStatus: async (id: string | number, isActive: boolean): Promise<User> => {
    const response = await apiClient.patch<ApiResponse<User>>(`/admin/users/${id}/status`, {
      isActive,
    });
    return response.data.data;
  },

  // Categories
  getCategories: async (): Promise<Category[]> => {
    const response = await apiClient.get<ApiResponse<Category[]>>('/admin/categories');
    return response.data.data || response.data;
  },

  getCategory: async (id: string | number): Promise<Category | null> => {
    try {
      const response = await apiClient.get<ApiResponse<Category>>(`/admin/categories/${id}`);
      return response.data.data;
    } catch (error) {
      console.error('Error fetching category:', error);
      return null;
    }
  },

  createCategory: async (data: Partial<Category>): Promise<Category> => {
    const response = await apiClient.post<ApiResponse<Category>>('/admin/categories', {
      name: data.name,
      description: data.description,
      image: data.image,
    });
    return response.data.data;
  },

  updateCategory: async (id: string | number, data: Partial<Category>): Promise<Category> => {
    const response = await apiClient.put<ApiResponse<Category>>(`/admin/categories/${id}`, {
      name: data.name,
      description: data.description,
      image: data.image,
    });
    return response.data.data;
  },

  deleteCategory: async (id: string | number): Promise<void> => {
    await apiClient.delete(`/admin/categories/${id}`);
  },

  // Inventory
  getInventory: async (filters?: {
    categoryId?: number;
    search?: string;
    page?: number;
    size?: number;
  }): Promise<Product[]> => {
    const response = await apiClient.get<ApiResponse<PageResponse<Product>>>('/admin/inventory', {
      params: {
        categoryId: filters?.categoryId,
        search: filters?.search,
        page: filters?.page || 0,
        size: filters?.size || 20,
      },
    });
    return response.data.data.content;
  },

  getLowStockProducts: async (): Promise<Product[]> => {
    const response = await apiClient.get<ApiResponse<Product[]>>('/admin/inventory/low-stock');
    return response.data.data;
  },

  getStockHistory: async (productId: string | number): Promise<any[]> => {
    const response = await apiClient.get<ApiResponse<any[]>>(`/admin/inventory/${productId}/history`);
    return response.data.data;
  },
};

export default adminApi;