import React from 'react';
import { Input } from '../../ui/input';
import { Label } from '../../ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../../ui/select';
import { Button } from '../../ui/button';
import { X } from 'lucide-react';

interface Filter {
  type: 'text' | 'select';
  name: string;
  label: string;
  placeholder?: string;
  options?: Array<{ value: string; label: string }>;
}

interface FilterPanelProps {
  filters: Filter[];
  values: Record<string, string>;
  onChange: (name: string, value: string) => void;
  onReset: () => void;
}

export const FilterPanel: React.FC<FilterPanelProps> = ({
  filters,
  values,
  onChange,
  onReset,
}) => {
  return (
    <div className="bg-white p-4 rounded-lg border border-gray-200 mb-6">
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {filters.map((filter) => (
          <div key={filter.name}>
            <Label htmlFor={filter.name}>{filter.label}</Label>
            {filter.type === 'text' ? (
              <Input
                id={filter.name}
                placeholder={filter.placeholder}
                value={values[filter.name] || ''}
                onChange={(e) => onChange(filter.name, e.target.value)}
              />
            ) : (
              <Select
                value={values[filter.name] || ''}
                onValueChange={(value) => onChange(filter.name, value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder={filter.placeholder} />
                </SelectTrigger>
                <SelectContent>
                  {filter.options?.map((option) => (
                    <SelectItem key={option.value} value={option.value}>
                      {option.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
          </div>
        ))}
        <div className="flex items-end">
          <Button variant="outline" onClick={onReset} className="w-full">
            <X className="w-4 h-4 mr-2" />
            Clear Filters
          </Button>
        </div>
      </div>
    </div>
  );
};
