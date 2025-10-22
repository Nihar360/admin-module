import React, { useState } from 'react';
import { AdminLayout } from '../components/admin/layout/AdminLayout';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import { Button } from '../components/ui/button';
import { Download, Calendar } from 'lucide-react';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '../components/ui/table';
import { Badge } from '../components/ui/badge';
import { toast } from 'sonner@2.0.3';

const auditLogs = [
  {
    id: '1',
    admin: 'Admin User',
    action: 'Updated order status',
    resource: 'Order #ORD-00123',
    timestamp: new Date().toISOString(),
  },
  {
    id: '2',
    admin: 'Admin User',
    action: 'Created product',
    resource: 'Product #PROD-456',
    timestamp: new Date(Date.now() - 3600000).toISOString(),
  },
  {
    id: '3',
    admin: 'Admin User',
    action: 'Updated coupon',
    resource: 'Coupon SUMMER25',
    timestamp: new Date(Date.now() - 7200000).toISOString(),
  },
];

export const ReportsPage: React.FC = () => {
  const handleExport = (type: string) => {
    toast(`Exporting ${type} report...`);
  };

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl">Reports</h1>
          <Button>
            <Calendar className="w-4 h-4 mr-2" />
            Date Range
          </Button>
        </div>

        <Tabs defaultValue="sales">
          <TabsList>
            <TabsTrigger value="sales">Sales Report</TabsTrigger>
            <TabsTrigger value="audit">Audit Logs</TabsTrigger>
          </TabsList>

          <TabsContent value="sales" className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>Total Revenue</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-3xl">$125,340</div>
                  <p className="text-sm text-gray-500 mt-2">Last 30 days</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Total Orders</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-3xl">1,234</div>
                  <p className="text-sm text-gray-500 mt-2">Last 30 days</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Average Order</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-3xl">$101.57</div>
                  <p className="text-sm text-gray-500 mt-2">Last 30 days</p>
                </CardContent>
              </Card>
            </div>

            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <CardTitle>Sales by Category</CardTitle>
                  <Button variant="outline" onClick={() => handleExport('sales')}>
                    <Download className="w-4 h-4 mr-2" />
                    Export
                  </Button>
                </div>
              </CardHeader>
              <CardContent>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Category</TableHead>
                      <TableHead>Orders</TableHead>
                      <TableHead>Revenue</TableHead>
                      <TableHead>Growth</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    <TableRow>
                      <TableCell>Electronics</TableCell>
                      <TableCell>342</TableCell>
                      <TableCell>$45,230</TableCell>
                      <TableCell>
                        <Badge variant="default">+12.5%</Badge>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell>Clothing</TableCell>
                      <TableCell>512</TableCell>
                      <TableCell>$38,450</TableCell>
                      <TableCell>
                        <Badge variant="default">+8.3%</Badge>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell>Home & Garden</TableCell>
                      <TableCell>201</TableCell>
                      <TableCell>$25,890</TableCell>
                      <TableCell>
                        <Badge variant="default">+5.7%</Badge>
                      </TableCell>
                    </TableRow>
                  </TableBody>
                </Table>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="audit" className="space-y-4">
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <CardTitle>Admin Activity Log</CardTitle>
                  <Button variant="outline" onClick={() => handleExport('audit')}>
                    <Download className="w-4 h-4 mr-2" />
                    Export
                  </Button>
                </div>
              </CardHeader>
              <CardContent>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Admin</TableHead>
                      <TableHead>Action</TableHead>
                      <TableHead>Resource</TableHead>
                      <TableHead>Timestamp</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {auditLogs.map((log) => (
                      <TableRow key={log.id}>
                        <TableCell>{log.admin}</TableCell>
                        <TableCell>{log.action}</TableCell>
                        <TableCell>{log.resource}</TableCell>
                        <TableCell>
                          {new Date(log.timestamp).toLocaleString()}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </AdminLayout>
  );
};
