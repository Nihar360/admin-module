// Mock API functions for admin operations
// In production, replace these with actual API calls

export interface Order {
  id: string;
  orderNumber: string;
  customer: {
    name: string;
    email: string;
    phone: string;
  };
  items: Array<{
    id: string;
    name: string;
    quantity: number;
    price: number;
    image: string;
  }>;
  total: number;
  status: 'pending' | 'processing' | 'shipped' | 'delivered' | 'cancelled';
  paymentStatus: 'pending' | 'paid' | 'refunded';
  shippingAddress: {
    street: string;
    city: string;
    state: string;
    zipCode: string;
    country: string;
  };
  createdAt: string;
  updatedAt: string;
}

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
  stock: number;
  sku: string;
  images: string[];
  isActive: boolean;
  createdAt: string;
}

export interface Coupon {
  id: string;
  code: string;
  type: 'percentage' | 'fixed';
  value: number;
  minPurchase: number;
  maxDiscount?: number;
  usageLimit: number;
  usageCount: number;
  expiresAt: string;
  isActive: boolean;
}

export interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  totalOrders: number;
  totalSpent: number;
  joinedAt: string;
  status: 'active' | 'blocked';
}

export interface DashboardStats {
  totalRevenue: number;
  totalOrders: number;
  totalCustomers: number;
  averageOrderValue: number;
  revenueChange: number;
  ordersChange: number;
  customersChange: number;
}

export interface SalesData {
  date: string;
  revenue: number;
  orders: number;
}

// Mock data
const mockOrders: Order[] = Array.from({ length: 50 }, (_, i) => ({
  id: `order-${i + 1}`,
  orderNumber: `ORD-${String(i + 1).padStart(5, '0')}`,
  customer: {
    name: `Customer ${i + 1}`,
    email: `customer${i + 1}@example.com`,
    phone: `+1234567${String(i).padStart(4, '0')}`,
  },
  items: [
    {
      id: `item-${i + 1}`,
      name: `Product ${i + 1}`,
      quantity: Math.floor(Math.random() * 3) + 1,
      price: Math.floor(Math.random() * 100) + 20,
      image: '',
    },
  ],
  total: Math.floor(Math.random() * 500) + 50,
  status: ['pending', 'processing', 'shipped', 'delivered', 'cancelled'][
    Math.floor(Math.random() * 5)
  ] as Order['status'],
  paymentStatus: ['pending', 'paid', 'refunded'][Math.floor(Math.random() * 3)] as Order['paymentStatus'],
  shippingAddress: {
    street: `${i + 1} Main Street`,
    city: 'New York',
    state: 'NY',
    zipCode: '10001',
    country: 'USA',
  },
  createdAt: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
  updatedAt: new Date().toISOString(),
}));

const mockProducts: Product[] = Array.from({ length: 30 }, (_, i) => ({
  id: `product-${i + 1}`,
  name: `Product ${i + 1}`,
  description: `This is a detailed description for Product ${i + 1}`,
  price: Math.floor(Math.random() * 200) + 20,
  category: ['Electronics', 'Clothing', 'Home & Garden', 'Sports', 'Books'][
    Math.floor(Math.random() * 5)
  ],
  stock: Math.floor(Math.random() * 100),
  sku: `SKU-${String(i + 1).padStart(5, '0')}`,
  images: [],
  isActive: Math.random() > 0.2,
  createdAt: new Date(Date.now() - Math.random() * 90 * 24 * 60 * 60 * 1000).toISOString(),
}));

const mockCoupons: Coupon[] = [
  {
    id: '1',
    code: 'SUMMER25',
    type: 'percentage',
    value: 25,
    minPurchase: 50,
    maxDiscount: 100,
    usageLimit: 1000,
    usageCount: 234,
    expiresAt: '2025-08-31T23:59:59Z',
    isActive: true,
  },
  {
    id: '2',
    code: 'FLAT50',
    type: 'fixed',
    value: 50,
    minPurchase: 200,
    usageLimit: 500,
    usageCount: 89,
    expiresAt: '2025-12-31T23:59:59Z',
    isActive: true,
  },
];

const mockUsers: User[] = Array.from({ length: 25 }, (_, i) => ({
  id: `user-${i + 1}`,
  name: `Customer ${i + 1}`,
  email: `customer${i + 1}@example.com`,
  phone: `+1234567${String(i).padStart(4, '0')}`,
  totalOrders: Math.floor(Math.random() * 20) + 1,
  totalSpent: Math.floor(Math.random() * 5000) + 100,
  joinedAt: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000).toISOString(),
  status: Math.random() > 0.1 ? 'active' : 'blocked',
}));

// API functions
export const adminApi = {
  // Dashboard
  getDashboardStats: async (): Promise<DashboardStats> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return {
      totalRevenue: 125340,
      totalOrders: 1234,
      totalCustomers: 567,
      averageOrderValue: 101.57,
      revenueChange: 12.5,
      ordersChange: 8.3,
      customersChange: 15.2,
    };
  },

  getSalesData: async (days: number = 30): Promise<SalesData[]> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return Array.from({ length: days }, (_, i) => ({
      date: new Date(Date.now() - (days - i - 1) * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      revenue: Math.floor(Math.random() * 5000) + 1000,
      orders: Math.floor(Math.random() * 50) + 10,
    }));
  },

  // Orders
  getOrders: async (filters?: {
    status?: string;
    search?: string;
    page?: number;
    limit?: number;
  }): Promise<{ orders: Order[]; total: number }> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    let filtered = [...mockOrders];

    if (filters?.status && filters.status !== 'all') {
      filtered = filtered.filter((order) => order.status === filters.status);
    }

    if (filters?.search) {
      const search = filters.search.toLowerCase();
      filtered = filtered.filter(
        (order) =>
          order.orderNumber.toLowerCase().includes(search) ||
          order.customer.name.toLowerCase().includes(search) ||
          order.customer.email.toLowerCase().includes(search)
      );
    }

    return { orders: filtered, total: filtered.length };
  },

  getOrder: async (id: string): Promise<Order | null> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return mockOrders.find((order) => order.id === id) || null;
  },

  updateOrderStatus: async (id: string, status: Order['status']): Promise<Order> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    const order = mockOrders.find((o) => o.id === id);
    if (!order) throw new Error('Order not found');
    order.status = status;
    order.updatedAt = new Date().toISOString();
    return order;
  },

  // Products
  getProducts: async (filters?: {
    category?: string;
    search?: string;
    inStock?: boolean;
  }): Promise<Product[]> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    let filtered = [...mockProducts];

    if (filters?.category && filters.category !== 'all') {
      filtered = filtered.filter((product) => product.category === filters.category);
    }

    if (filters?.search) {
      const search = filters.search.toLowerCase();
      filtered = filtered.filter(
        (product) =>
          product.name.toLowerCase().includes(search) ||
          product.sku.toLowerCase().includes(search)
      );
    }

    if (filters?.inStock) {
      filtered = filtered.filter((product) => product.stock > 0);
    }

    return filtered;
  },

  getProduct: async (id: string): Promise<Product | null> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return mockProducts.find((product) => product.id === id) || null;
  },

  createProduct: async (data: Omit<Product, 'id' | 'createdAt'>): Promise<Product> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    const newProduct: Product = {
      ...data,
      id: `product-${mockProducts.length + 1}`,
      createdAt: new Date().toISOString(),
    };
    mockProducts.push(newProduct);
    return newProduct;
  },

  updateProduct: async (id: string, data: Partial<Product>): Promise<Product> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    const product = mockProducts.find((p) => p.id === id);
    if (!product) throw new Error('Product not found');
    Object.assign(product, data);
    return product;
  },

  deleteProduct: async (id: string): Promise<void> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    const index = mockProducts.findIndex((p) => p.id === id);
    if (index > -1) {
      mockProducts.splice(index, 1);
    }
  },

  // Coupons
  getCoupons: async (): Promise<Coupon[]> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return mockCoupons;
  },

  createCoupon: async (data: Omit<Coupon, 'id' | 'usageCount'>): Promise<Coupon> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    const newCoupon: Coupon = {
      ...data,
      id: `coupon-${mockCoupons.length + 1}`,
      usageCount: 0,
    };
    mockCoupons.push(newCoupon);
    return newCoupon;
  },

  updateCoupon: async (id: string, data: Partial<Coupon>): Promise<Coupon> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    const coupon = mockCoupons.find((c) => c.id === id);
    if (!coupon) throw new Error('Coupon not found');
    Object.assign(coupon, data);
    return coupon;
  },

  deleteCoupon: async (id: string): Promise<void> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    const index = mockCoupons.findIndex((c) => c.id === id);
    if (index > -1) {
      mockCoupons.splice(index, 1);
    }
  },

  // Users
  getUsers: async (filters?: { search?: string; status?: string }): Promise<User[]> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    let filtered = [...mockUsers];

    if (filters?.search) {
      const search = filters.search.toLowerCase();
      filtered = filtered.filter(
        (user) =>
          user.name.toLowerCase().includes(search) || user.email.toLowerCase().includes(search)
      );
    }

    if (filters?.status && filters.status !== 'all') {
      filtered = filtered.filter((user) => user.status === filters.status);
    }

    return filtered;
  },

  getUser: async (id: string): Promise<User | null> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return mockUsers.find((user) => user.id === id) || null;
  },
};
