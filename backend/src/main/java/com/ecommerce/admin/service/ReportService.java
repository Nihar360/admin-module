package com.ecommerce.admin.service;

import com.ecommerce.admin.repository.OrderItemRepository;
import com.ecommerce.admin.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Transactional(readOnly = true)
    public SalesReportResponse getSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        if (startDate.isAfter(endDate)) {
            LocalDateTime temp = startDate;
            startDate = endDate;
            endDate = temp;
        }
        
        BigDecimal totalRevenue = orderRepository.getTotalRevenue(startDate, endDate);
        Long totalOrders = orderRepository.countSuccessfulOrders(startDate, endDate);
        
        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (totalOrders > 0) {
            averageOrderValue = totalRevenue.divide(
                    BigDecimal.valueOf(totalOrders), 
                    2, 
                    BigDecimal.ROUND_HALF_UP
            );
        }
        
        List<DailySalesData> dailySales = new ArrayList<>();
        for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);
            
            BigDecimal dayRevenue = orderRepository.getTotalRevenue(dayStart, dayEnd);
            Long dayOrders = orderRepository.countSuccessfulOrders(dayStart, dayEnd);
            
            dailySales.add(DailySalesData.builder()
                    .date(dayStart.format(DATE_FORMATTER))
                    .revenue(dayRevenue)
                    .orders(dayOrders)
                    .build());
        }
        
        log.info("Generated sales report from {} to {}: revenue={}, orders={}", 
                startDate, endDate, totalRevenue, totalOrders);
        
        return SalesReportResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .averageOrderValue(averageOrderValue)
                .dailySales(dailySales)
                .build();
    }
    
    @Transactional(readOnly = true)
    public List<TopProductResponse> getTopProducts(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        
        List<Object[]> topProductsData = orderItemRepository.findTopSellingProducts(
                PageRequest.of(0, limit)
        );
        
        List<TopProductResponse> topProducts = new ArrayList<>();
        for (Object[] data : topProductsData) {
            topProducts.add(TopProductResponse.builder()
                    .productId((Long) data[0])
                    .productName((String) data[1])
                    .thumbnail((String) data[2])
                    .totalQuantitySold(((Number) data[3]).longValue())
                    .totalRevenue((BigDecimal) data[4])
                    .build());
        }
        
        log.info("Retrieved top {} products", topProducts.size());
        
        return topProducts;
    }
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class SalesReportResponse {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private BigDecimal averageOrderValue;
    private List<DailySalesData> dailySales;
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class DailySalesData {
    private String date;
    private BigDecimal revenue;
    private Long orders;
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class TopProductResponse {
    private Long productId;
    private String productName;
    private String thumbnail;
    private Long totalQuantitySold;
    private BigDecimal totalRevenue;
}
