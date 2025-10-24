import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AdminLayout } from '../components/admin/layout/AdminLayout';
import { DataTable } from '../components/admin/shared/DataTable';
import { FilterPanel } from '../components/admin/shared/FilterPanel';
import { StockBadge } from '../components/admin/products/StockBadge';
import { ConfirmDialog } from '../components/admin/shared/ConfirmDialog';
import { useAdminProducts } from '../hooks/useAdminProducts';
import { useCategories } from '../hooks/useCategories';
import { Product } from '../api/adminApi';
import { Plus, Edit, Trash2 } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { toast } from 'sonner';

export const ProductList: React.FC = () => {
  const [filters, setFilters] = useState({
    category: 'all',
    search: '',
  });
  const [deleteId, setDeleteId] = useState<string | number | null>(null);
  const [confirmStep, setConfirmStep] = useState<1 | 2>(1);
  const { products, isLoading, deleteProduct } = useAdminProducts(filters);
  const { categories } = useCategories();
  const navigate = useNavigate();

  const handleFilterChange = (name: string, value: string) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleReset = () => {
    setFilters({ category: 'all', search: '' });
  };

  const handleConfirmClick = () => {
    if (confirmStep === 1) {
      setConfirmStep(2);
    } else {
      handleDelete();
    }
  };

  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      await deleteProduct(deleteId);
      toast.success('Product deleted successfully');
      setDeleteId(null);
      setConfirmStep(1);
    } catch (error) {
      toast.error('Failed to delete product');
    }
  };

  const handleDialogClose = (open: boolean) => {
    if (!open) {
      setDeleteId(null);
      setConfirmStep(1);
    }
  };

  const columns = [
    {
      key: 'name',
      header: 'Product',
      render: (product: Product) => (
        <div>
          <div>{product.name}</div>
          <div className="text-sm text-gray-500">{product.sku}</div>
        </div>
      ),
    },
    {
      key: 'category',
      header: 'Category',
    },
    {
      key: 'price',
      header: 'Price',
      render: (product: Product) => `$${product.price.toFixed(2)}`,
    },
    {
      key: 'stock',
      header: 'Stock',
      render: (product: Product) => (
        <div className="flex items-center gap-2">
          <span>{product.stockCount}</span>
          <StockBadge stock={product.stockCount} />
        </div>
      ),
    },
    {
      key: 'status',
      header: 'Status',
      render: (product: Product) => (
        <Badge variant={product.isActive ? 'default' : 'secondary'}>
          {product.isActive ? 'Active' : 'Inactive'}
        </Badge>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (product: Product) => (
        <div className="flex gap-2">
          <Button
            size="sm"
            variant="ghost"
            onClick={(e: React.MouseEvent) => {
              e.stopPropagation();
              navigate(`/admin/products/${product.id}/edit`);
            }}
          >
            <Edit className="w-4 h-4" />
          </Button>
          <Button
            size="sm"
            variant="ghost"
            onClick={(e: React.MouseEvent) => {
              e.stopPropagation();
              setDeleteId(product.id);
            }}
          >
            <Trash2 className="w-4 h-4 text-red-600" />
          </Button>
        </div>
      ),
    },
  ];

  const filterConfig = [
    {
      type: 'text' as const,
      name: 'search',
      label: 'Search',
      placeholder: 'Search products...',
    },
    {
      type: 'select' as const,
      name: 'category',
      label: 'Category',
      placeholder: 'All Categories',
      options: [
        { value: 'all', label: 'All Categories' },
        ...categories.map(cat => ({
          value: cat.id.toString(),
          label: cat.name
        }))
      ],
    },
  ];

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl">Products</h1>
          <Button onClick={() => navigate('/admin/products/new')}>
            <Plus className="w-4 h-4 mr-2" />
            Add Product
          </Button>
        </div>

        <FilterPanel
          filters={filterConfig}
          values={filters}
          onChange={handleFilterChange}
          onReset={handleReset}
        />

        {isLoading ? (
          <div className="text-center py-8">Loading products...</div>
        ) : (
          <DataTable data={products} columns={columns} />
        )}
      </div>

      <ConfirmDialog
        open={!!deleteId}
        onOpenChange={handleDialogClose}
        title={confirmStep === 1 ? "Delete Product" : "Final Confirmation"}
        description={
          confirmStep === 1
            ? "Are you sure you want to delete this product?"
            : "This action cannot be undone. All product data will be permanently deleted."
        }
        onConfirm={handleConfirmClick}
        confirmText={confirmStep === 1 ? "Continue" : "Delete Permanently"}
      />
    </AdminLayout>
  );
};
