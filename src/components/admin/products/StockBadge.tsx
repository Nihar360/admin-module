import React from 'react';
import { Badge } from '../../ui/badge';

interface StockBadgeProps {
  stock: number;
}

export const StockBadge: React.FC<StockBadgeProps> = ({ stock }) => {
  if (stock === 0) {
    return <Badge variant="destructive">Out of Stock</Badge>;
  }

  if (stock < 10) {
    return <Badge variant="secondary">Low Stock</Badge>;
  }

  return <Badge variant="default">In Stock</Badge>;
};
