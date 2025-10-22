import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../../ui/card';
import { ArrowUp, ArrowDown } from 'lucide-react';
import { cn } from '../../ui/utils';

interface StatCardProps {
  title: string;
  value: string | number;
  change?: number;
  icon: React.ReactNode;
  prefix?: string;
  suffix?: string;
}

export const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  change,
  icon,
  prefix = '',
  suffix = '',
}) => {
  const isPositive = change && change > 0;
  const isNegative = change && change < 0;

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <CardTitle className="text-sm">{title}</CardTitle>
        <div className="text-gray-500">{icon}</div>
      </CardHeader>
      <CardContent>
        <div className="text-2xl">
          {prefix}
          {value}
          {suffix}
        </div>
        {change !== undefined && (
          <div className="flex items-center gap-1 mt-2 text-sm">
            {isPositive && (
              <>
                <ArrowUp className="w-4 h-4 text-green-600" />
                <span className="text-green-600">+{change}%</span>
              </>
            )}
            {isNegative && (
              <>
                <ArrowDown className="w-4 h-4 text-red-600" />
                <span className="text-red-600">{change}%</span>
              </>
            )}
            {!isPositive && !isNegative && (
              <span className="text-gray-500">0%</span>
            )}
            <span className="text-gray-500 ml-1">vs last month</span>
          </div>
        )}
      </CardContent>
    </Card>
  );
};
