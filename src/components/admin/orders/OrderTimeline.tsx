import React from 'react';
import { CheckCircle, Circle } from 'lucide-react';
import { Order } from '../../../api/adminApi';

interface OrderTimelineProps {
  order: Order;
}

const timelineSteps = [
  { key: 'pending', label: 'Order Placed' },
  { key: 'processing', label: 'Processing' },
  { key: 'shipped', label: 'Shipped' },
  { key: 'delivered', label: 'Delivered' },
];

export const OrderTimeline: React.FC<OrderTimelineProps> = ({ order }) => {
  const statusIndex = timelineSteps.findIndex((step) => step.key === order.status);

  return (
    <div className="space-y-4">
      {timelineSteps.map((step, index) => {
        const isCompleted = index <= statusIndex;
        const isCurrent = index === statusIndex;

        return (
          <div key={step.key} className="flex items-start gap-4">
            <div className="flex flex-col items-center">
              {isCompleted ? (
                <CheckCircle className="w-6 h-6 text-green-600" />
              ) : (
                <Circle className="w-6 h-6 text-gray-300" />
              )}
              {index < timelineSteps.length - 1 && (
                <div className={`w-0.5 h-8 ${isCompleted ? 'bg-green-600' : 'bg-gray-300'}`} />
              )}
            </div>
            <div>
              <div className={isCurrent ? '' : 'text-gray-500'}>
                {step.label}
              </div>
              {isCurrent && (
                <div className="text-sm text-gray-500">
                  {new Date(order.updatedAt).toLocaleString()}
                </div>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};
