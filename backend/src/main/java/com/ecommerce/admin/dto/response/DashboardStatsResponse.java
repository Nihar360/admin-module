package com.ecommerce.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalCustomers;
    private Long pendingOrders;
    
    private Double revenueChange;
    private Double ordersChange;
    private Double customersChange;
    
    private List<SalesDataPoint> salesData;
    private List<OrderResponse> recentOrders;
    private List<ProductResponse> lowStockProducts;
}
