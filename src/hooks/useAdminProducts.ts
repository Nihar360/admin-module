import { useState, useEffect } from 'react';
import { adminApi, Product } from '../api/adminApi';

export const useAdminProducts = (filters?: {
  category?: string;
  search?: string;
  inStock?: boolean;
}) => {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadProducts();
  }, [filters?.category, filters?.search, filters?.inStock]);

  const loadProducts = async () => {
    try {
      setIsLoading(true);
      const data = await adminApi.getProducts(filters);
      setProducts(data);
      setError(null);
    } catch (err) {
      setError('Failed to load products');
    } finally {
      setIsLoading(false);
    }
  };

  const createProduct = async (data: Omit<Product, 'id' | 'createdAt'>) => {
    try {
      await adminApi.createProduct(data);
      await loadProducts();
    } catch (err) {
      throw new Error('Failed to create product');
    }
  };

  const updateProduct = async (id: string, data: Partial<Product>) => {
    try {
      await adminApi.updateProduct(id, data);
      await loadProducts();
    } catch (err) {
      throw new Error('Failed to update product');
    }
  };

  const deleteProduct = async (id: string) => {
    try {
      await adminApi.deleteProduct(id);
      await loadProducts();
    } catch (err) {
      throw new Error('Failed to delete product');
    }
  };

  return {
    products,
    isLoading,
    error,
    reload: loadProducts,
    createProduct,
    updateProduct,
    deleteProduct,
  };
};
