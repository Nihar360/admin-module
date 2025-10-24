import React from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

interface Column<T> {
  key: string;
  header: string;
  render?: (item: T) => React.ReactNode;
}

interface DataTableProps<T> {
  data: T[];
  columns: Column<T>[];
  onRowClick?: (item: T) => void;
}

// FIX: Add proper generic constraint
export function DataTable<T extends Record<string, any>>({
  data,
  columns,
  onRowClick,
}: DataTableProps<T>) {
  console.log('📊 DataTable rendering with data:', data);
  console.log('📊 Number of rows:', data?.length || 0);

  // Safety check for data
  if (!data) {
    console.warn('⚠️ DataTable received null/undefined data');
    return (
      <div className="text-center py-8 text-gray-500">
        No data available
      </div>
    );
  }

  if (!Array.isArray(data)) {
    console.error('❌ DataTable received non-array data:', data);
    return (
      <div className="text-center py-8 text-red-500">
        Invalid data format
      </div>
    );
  }

  if (data.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        No records found
      </div>
    );
  }

  return (
    <div className="border rounded-lg">
      <Table>
        <TableHeader>
          <TableRow>
            {columns.map((column) => (
              <TableHead key={column.key}>{column.header}</TableHead>
            ))}
          </TableRow>
        </TableHeader>
        <TableBody>
          {data.map((item, index) => {
            // Generate a unique key
            const rowKey = (item as any).id || (item as any).key || index;
            
            return (
              <TableRow
                key={rowKey}
                onClick={() => onRowClick?.(item)}
                className={onRowClick ? 'cursor-pointer hover:bg-gray-50' : ''}
              >
                {columns.map((column) => {
                  let cellContent;
                  
                  try {
                    if (column.render) {
                      // Use custom render function
                      cellContent = column.render(item);
                    } else if (column.key in item) {
                      // Use direct property access
                      cellContent = String((item as any)[column.key] ?? '-');
                    } else {
                      // Property doesn't exist
                      cellContent = '-';
                    }
                  } catch (error) {
                    console.error(`❌ Error rendering column ${column.key}:`, error);
                    console.error('❌ Item:', item);
                    cellContent = 'Error';
                  }

                  return (
                    <TableCell key={column.key}>
                      {cellContent}
                    </TableCell>
                  );
                })}
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
    </div>
  );
}