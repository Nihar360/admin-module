import React, { useState, useEffect } from 'react';
import { AdminLayout } from '../components/admin/layout/AdminLayout';
import { DataTable } from '../components/admin/shared/DataTable';
import { ConfirmDialog } from '../components/admin/shared/ConfirmDialog';
import { adminApi, Coupon } from '../api/adminApi';
import { Plus, Edit, Trash2 } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { Progress } from '../components/ui/progress';
import { toast } from 'sonner@2.0.3';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '../components/ui/dialog';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../components/ui/select';
import { Switch } from '../components/ui/switch';

export const CouponList: React.FC = () => {
  const [coupons, setCoupons] = useState<Coupon[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editCoupon, setEditCoupon] = useState<Coupon | null>(null);

  useEffect(() => {
    loadCoupons();
  }, []);

  const loadCoupons = async () => {
    try {
      const data = await adminApi.getCoupons();
      setCoupons(data);
    } catch (error) {
      toast('Failed to load coupons');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      await adminApi.deleteCoupon(deleteId);
      toast('Coupon deleted successfully');
      setDeleteId(null);
      loadCoupons();
    } catch (error) {
      toast('Failed to delete coupon');
    }
  };

  const columns = [
    {
      key: 'code',
      header: 'Code',
      render: (coupon: Coupon) => (
        <span className="font-mono">{coupon.code}</span>
      ),
    },
    {
      key: 'type',
      header: 'Type',
      render: (coupon: Coupon) => (
        <Badge variant="outline">
          {coupon.type === 'percentage' ? 'Percentage' : 'Fixed Amount'}
        </Badge>
      ),
    },
    {
      key: 'value',
      header: 'Discount',
      render: (coupon: Coupon) =>
        coupon.type === 'percentage'
          ? `${coupon.value}%`
          : `$${coupon.value}`,
    },
    {
      key: 'usage',
      header: 'Usage',
      render: (coupon: Coupon) => (
        <div className="space-y-1">
          <div className="text-sm">
            {coupon.usageCount} / {coupon.usageLimit}
          </div>
          <Progress
            value={(coupon.usageCount / coupon.usageLimit) * 100}
            className="h-2"
          />
        </div>
      ),
    },
    {
      key: 'expiresAt',
      header: 'Expires',
      render: (coupon: Coupon) => new Date(coupon.expiresAt).toLocaleDateString(),
    },
    {
      key: 'status',
      header: 'Status',
      render: (coupon: Coupon) => (
        <Badge variant={coupon.isActive ? 'default' : 'secondary'}>
          {coupon.isActive ? 'Active' : 'Inactive'}
        </Badge>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (coupon: Coupon) => (
        <div className="flex gap-2">
          <Button
            size="sm"
            variant="ghost"
            onClick={(e) => {
              e.stopPropagation();
              setEditCoupon(coupon);
              setShowForm(true);
            }}
          >
            <Edit className="w-4 h-4" />
          </Button>
          <Button
            size="sm"
            variant="ghost"
            onClick={(e) => {
              e.stopPropagation();
              setDeleteId(coupon.id);
            }}
          >
            <Trash2 className="w-4 h-4 text-red-600" />
          </Button>
        </div>
      ),
    },
  ];

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl">Coupons</h1>
          <Button
            onClick={() => {
              setEditCoupon(null);
              setShowForm(true);
            }}
          >
            <Plus className="w-4 h-4 mr-2" />
            Add Coupon
          </Button>
        </div>

        {isLoading ? (
          <div className="text-center py-8">Loading coupons...</div>
        ) : (
          <DataTable data={coupons} columns={columns} />
        )}
      </div>

      <CouponFormDialog
        open={showForm}
        onOpenChange={setShowForm}
        coupon={editCoupon}
        onSuccess={loadCoupons}
      />

      <ConfirmDialog
        open={!!deleteId}
        onOpenChange={(open) => !open && setDeleteId(null)}
        title="Delete Coupon"
        description="Are you sure you want to delete this coupon? This action cannot be undone."
        onConfirm={handleDelete}
        confirmText="Delete"
      />
    </AdminLayout>
  );
};

interface CouponFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  coupon: Coupon | null;
  onSuccess: () => void;
}

const CouponFormDialog: React.FC<CouponFormDialogProps> = ({
  open,
  onOpenChange,
  coupon,
  onSuccess,
}) => {
  const [formData, setFormData] = useState({
    code: '',
    type: 'percentage' as 'percentage' | 'fixed',
    value: '',
    minPurchase: '',
    maxDiscount: '',
    usageLimit: '',
    expiresAt: '',
    isActive: true,
  });

  useEffect(() => {
    if (coupon) {
      setFormData({
        code: coupon.code,
        type: coupon.type,
        value: coupon.value.toString(),
        minPurchase: coupon.minPurchase.toString(),
        maxDiscount: coupon.maxDiscount?.toString() || '',
        usageLimit: coupon.usageLimit.toString(),
        expiresAt: coupon.expiresAt.split('T')[0],
        isActive: coupon.isActive,
      });
    } else {
      setFormData({
        code: '',
        type: 'percentage',
        value: '',
        minPurchase: '',
        maxDiscount: '',
        usageLimit: '',
        expiresAt: '',
        isActive: true,
      });
    }
  }, [coupon, open]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const data = {
        code: formData.code,
        type: formData.type,
        value: parseFloat(formData.value),
        minPurchase: parseFloat(formData.minPurchase),
        maxDiscount: formData.maxDiscount ? parseFloat(formData.maxDiscount) : undefined,
        usageLimit: parseInt(formData.usageLimit),
        expiresAt: new Date(formData.expiresAt).toISOString(),
        isActive: formData.isActive,
      };

      if (coupon) {
        await adminApi.updateCoupon(coupon.id, data);
        toast('Coupon updated successfully');
      } else {
        await adminApi.createCoupon(data);
        toast('Coupon created successfully');
      }
      onSuccess();
      onOpenChange(false);
    } catch (error) {
      toast('Failed to save coupon');
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>{coupon ? 'Edit Coupon' : 'Create Coupon'}</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="code">Coupon Code</Label>
              <Input
                id="code"
                value={formData.code}
                onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="type">Type</Label>
              <Select
                value={formData.type}
                onValueChange={(value: 'percentage' | 'fixed') =>
                  setFormData({ ...formData, type: value })
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="percentage">Percentage</SelectItem>
                  <SelectItem value="fixed">Fixed Amount</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="value">
                {formData.type === 'percentage' ? 'Percentage' : 'Amount'}
              </Label>
              <Input
                id="value"
                type="number"
                step="0.01"
                value={formData.value}
                onChange={(e) => setFormData({ ...formData, value: e.target.value })}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="minPurchase">Minimum Purchase</Label>
              <Input
                id="minPurchase"
                type="number"
                step="0.01"
                value={formData.minPurchase}
                onChange={(e) => setFormData({ ...formData, minPurchase: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="maxDiscount">Max Discount (Optional)</Label>
              <Input
                id="maxDiscount"
                type="number"
                step="0.01"
                value={formData.maxDiscount}
                onChange={(e) => setFormData({ ...formData, maxDiscount: e.target.value })}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="usageLimit">Usage Limit</Label>
              <Input
                id="usageLimit"
                type="number"
                value={formData.usageLimit}
                onChange={(e) => setFormData({ ...formData, usageLimit: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="expiresAt">Expiry Date</Label>
            <Input
              id="expiresAt"
              type="date"
              value={formData.expiresAt}
              onChange={(e) => setFormData({ ...formData, expiresAt: e.target.value })}
              required
            />
          </div>

          <div className="flex items-center space-x-2">
            <Switch
              id="isActive"
              checked={formData.isActive}
              onCheckedChange={(checked) => setFormData({ ...formData, isActive: checked })}
            />
            <Label htmlFor="isActive">Coupon is active</Label>
          </div>

          <div className="flex gap-4 pt-4">
            <Button type="submit">
              {coupon ? 'Update Coupon' : 'Create Coupon'}
            </Button>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};
