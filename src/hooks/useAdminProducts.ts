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
      const pageResponse = await adminApi.getProducts(filters);
      
      // Debug logs
      console.log('📦 Product PageResponse:', pageResponse);
      console.log('📋 Product Content:', pageResponse.content);
      console.log('✅ Is Product Array?:', Array.isArray(pageResponse.content));
      console.log('✅ Product count:', pageResponse.content?.length || 0);
      
      // Extract the content array from PageResponse
      setProducts(pageResponse.content || []);
      setError(null);
    } catch (err: any) {
      console.error('❌ Product Error:', err);
      console.error('❌ Error details:', err.response?.data);
      setError('Failed to load products');
      setProducts([]); // Set empty array on error
    } finally {
      setIsLoading(false);
    }
  };
  const createProduct = async (data: Omit<Product, 'id' | 'createdAt'>) => {
    try {
      console.log('➕ Creating product:', data);
      await adminApi.createProduct(data);
      console.log('✅ Product created successfully');
      await loadProducts();
    } catch (err: any) {
      console.error('❌ Error creating product:', err);
      throw new Error('Failed to create product');
    }
  };
  const updateProduct = async (id: string, data: Partial<Product>) => {
    try {
      console.log('🔄 Updating product:', { id, data });
      await adminApi.updateProduct(id, data);
      console.log('✅ Product updated successfully');
      await loadProducts();
    } catch (err: any) {
      console.error('❌ Error updating product:', err);
      throw new Error('Failed to update product');
    }
  };
  const deleteProduct = async (id: string) => {
    try {
      console.log('🗑️ Deleting product:', id);
      await adminApi.deleteProduct(id);
      console.log('✅ Product deleted successfully');
      await loadProducts();
    } catch (err: any) {
      console.error('❌ Error deleting product:', err);
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