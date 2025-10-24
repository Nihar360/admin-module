import React, { useState, useEffect } from 'react';
import { AdminLayout } from '../components/admin/layout/AdminLayout';
import { DataTable } from '../components/admin/shared/DataTable';
import { StockBadge } from '../components/admin/products/StockBadge';
import { useAdminProducts } from '../hooks/useAdminProducts';
import { Product } from '../api/adminApi';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '../components/ui/dialog';
import { AlertCircle, PackagePlus, PackageMinus } from 'lucide-react';
import { toast } from 'sonner';

export const InventoryManagement: React.FC = () => {
  const { products, isLoading, updateProduct } = useAdminProducts();
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [showDialog, setShowDialog] = useState(false);
  const [adjustmentType, setAdjustmentType] = useState<'add' | 'remove'>('add');
  const [quantity, setQuantity] = useState('');

  const lowStockProducts = products.filter((p) => p.stockCount < 10 && p.stockCount > 0);
  const outOfStockProducts = products.filter((p) => p.stockCount === 0);

  const handleAdjustment = async () => {
    if (!selectedProduct || !quantity) return;

    const adjustment = parseInt(quantity);
    const newStock =
      adjustmentType === 'add'
        ? selectedProduct.stockCount + adjustment
        : selectedProduct.stockCount - adjustment;

    if (newStock < 0) {
      toast.error('Stock cannot be negative');
      return;
    }

    try {
      await updateProduct(selectedProduct.id, { stockCount: newStock });
      toast.success('Stock updated successfully');
      setShowDialog(false);
      setQuantity('');
    } catch (error) {
      toast.error('Failed to update stock');
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
      key: 'stockCount',
      header: 'Current Stock',
      render: (product: Product) => (
        <div className="flex items-center gap-2">
          <span>{product.stockCount}</span>
          <StockBadge stock={product.stockCount} />
        </div>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (product: Product) => (
        <div className="flex gap-2">
          <Button
            size="sm"
            variant="outline"
            onClick={(e) => {
              e.stopPropagation();
              setSelectedProduct(product);
              setAdjustmentType('add');
              setShowDialog(true);
            }}
          >
            <PackagePlus className="w-4 h-4 mr-2" />
            Add Stock
          </Button>
          <Button
            size="sm"
            variant="outline"
            onClick={(e) => {
              e.stopPropagation();
              setSelectedProduct(product);
              setAdjustmentType('remove');
              setShowDialog(true);
            }}
          >
            <PackageMinus className="w-4 h-4 mr-2" />
            Remove Stock
          </Button>
        </div>
      ),
    },
  ];

  return (
    <AdminLayout>
      <div className="space-y-6">
        <h1 className="text-3xl">Inventory Management</h1>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <AlertCircle className="w-5 h-5 text-yellow-600" />
                Low Stock Alert
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                {lowStockProducts.length === 0 ? (
                  <p className="text-gray-500">No low stock items</p>
                ) : (
                  lowStockProducts.map((product) => (
                    <div
                      key={product.id}
                      className="flex items-center justify-between p-2 border rounded"
                    >
                      <div>
                        <div>{product.name}</div>
                        <div className="text-sm text-gray-500">Stock: {product.stockCount}</div>
                      </div>
                      <StockBadge stock={product.stockCount} />
                    </div>
                  ))
                )}
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <AlertCircle className="w-5 h-5 text-red-600" />
                Out of Stock
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                {outOfStockProducts.length === 0 ? (
                  <p className="text-gray-500">No out of stock items</p>
                ) : (
                  outOfStockProducts.map((product) => (
                    <div
                      key={product.id}
                      className="flex items-center justify-between p-2 border rounded"
                    >
                      <div>
                        <div>{product.name}</div>
                        <div className="text-sm text-gray-500">{product.sku}</div>
                      </div>
                      <StockBadge stock={product.stockCount} />
                    </div>
                  ))
                )}
              </div>
            </CardContent>
          </Card>
        </div>

        {isLoading ? (
          <div className="text-center py-8">Loading inventory...</div>
        ) : (
          <DataTable data={products} columns={columns} />
        )}
      </div>

      <Dialog open={showDialog} onOpenChange={setShowDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {adjustmentType === 'add' ? 'Add Stock' : 'Remove Stock'}
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <p className="text-sm text-gray-500">Product</p>
              <p>{selectedProduct?.name}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Current Stock</p>
              <p>{selectedProduct?.stockCount}</p>
            </div>
            <div className="space-y-2">
              <Label htmlFor="quantity">Quantity</Label>
              <Input
                id="quantity"
                type="number"
                min="1"
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
                placeholder="Enter quantity"
              />
            </div>
            <div className="flex gap-4">
              <Button onClick={handleAdjustment} disabled={!quantity}>
                Confirm
              </Button>
              <Button variant="outline" onClick={() => setShowDialog(false)}>
                Cancel
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </AdminLayout>
  );
};
