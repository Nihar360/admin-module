import React, { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '../../ui/dialog';
import { Button } from '../../ui/button';
import { Label } from '../../ui/label';
import { Textarea } from '../../ui/textarea';
import { Input } from '../../ui/input';

interface RefundModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  orderTotal: number;
  onConfirm: (amount: number, reason: string) => void;
}

export const RefundModal: React.FC<RefundModalProps> = ({
  open,
  onOpenChange,
  orderTotal,
  onConfirm,
}) => {
  const [amount, setAmount] = useState(orderTotal.toString());
  const [reason, setReason] = useState('');

  const handleConfirm = () => {
    onConfirm(parseFloat(amount), reason);
    onOpenChange(false);
    setReason('');
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Process Refund</DialogTitle>
          <DialogDescription>
            Enter the refund amount and reason for this order.
          </DialogDescription>
        </DialogHeader>
        <div className="space-y-4 py-4">
          <div>
            <Label htmlFor="amount">Refund Amount</Label>
            <Input
              id="amount"
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              max={orderTotal}
              step="0.01"
            />
            <p className="text-sm text-gray-500 mt-1">
              Maximum: ${orderTotal.toFixed(2)}
            </p>
          </div>
          <div>
            <Label htmlFor="reason">Reason</Label>
            <Textarea
              id="reason"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="Enter reason for refund..."
              rows={4}
            />
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button onClick={handleConfirm} disabled={!reason.trim()}>
            Process Refund
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
