package com.ecommerce.admin.service;

import com.ecommerce.admin.dto.response.DashboardStatsResponse;
import com.ecommerce.admin.dto.response.OrderResponse;
import com.ecommerce.admin.dto.response.ProductResponse;
import com.ecommerce.admin.dto.response.SalesDataPoint;
import com.ecommerce.admin.model.Order;
import com.ecommerce.admin.model.Product;
import com.ecommerce.admin.model.enums.OrderStatus;
import com.ecommerce.admin.model.enums.UserRole;
import com.ecommerce.admin.repository.OrderRepository;
import com.ecommerce.admin.repository.ProductRepository;
import com.ecommerce.admin.repository.UserRepository;
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
public class DashboardService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(Integer days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(days != null ? days : 30);
        LocalDateTime prevStartDate = startDate.minusDays(days != null ? days : 30);
        
        BigDecimal currentRevenue = orderRepository.getTotalRevenue(startDate, now);
        Long currentOrders = orderRepository.countSuccessfulOrders(startDate, now);
        Long totalCustomers = userRepository.countByRole(UserRole.CUSTOMER);
        Long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        
        BigDecimal prevRevenue = orderRepository.getTotalRevenue(prevStartDate, startDate);
        Long prevOrders = orderRepository.countSuccessfulOrders(prevStartDate, startDate);
        
        Double revenueChange = calculatePercentageChange(currentRevenue, prevRevenue);
        Double ordersChange = calculatePercentageChange(
                BigDecimal.valueOf(currentOrders), 
                BigDecimal.valueOf(prevOrders)
        );
        
        List<SalesDataPoint> salesData = generateSalesData(startDate, now);
        List<OrderResponse> recentOrders = getRecentOrders(10);
        List<ProductResponse> lowStockProducts = getLowStockProducts();
        
        return DashboardStatsResponse.builder()
                .totalRevenue(currentRevenue)
                .totalOrders(currentOrders)
                .totalCustomers(totalCustomers)
                .pendingOrders(pendingOrders)
                .revenueChange(revenueChange)
                .ordersChange(ordersChange)
                .salesData(salesData)
                .recentOrders(recentOrders)
                .lowStockProducts(lowStockProducts)
                .build();
    }
    
    private Double calculatePercentageChange(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return current.subtract(previous)
                .divide(previous, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
    
    private List<SalesDataPoint> generateSalesData(LocalDateTime start, LocalDateTime end) {
        List<SalesDataPoint> salesData = new ArrayList<>();
        
        for (LocalDateTime date = start; date.isBefore(end); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);
            
            BigDecimal revenue = orderRepository.getTotalRevenue(dayStart, dayEnd);
            Long orders = orderRepository.countSuccessfulOrders(dayStart, dayEnd);
            
            salesData.add(SalesDataPoint.builder()
                    .date(dayStart.format(DATE_FORMATTER))
                    .revenue(revenue)
                    .orders(orders)
                    .build());
        }
        
        return salesData;
    }
    
    private List<OrderResponse> getRecentOrders(int limit) {
        List<Order> orders = orderRepository.findAll(
                PageRequest.of(0, limit)).getContent();
        
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(mapToOrderResponse(order));
        }
        return responses;
    }
    
    private List<ProductResponse> getLowStockProducts() {
        List<Product> products = productRepository.findByStockQuantityLessThan(10);
        
        List<ProductResponse> responses = new ArrayList<>();
        for (Product product : products) {
            responses.add(mapToProductResponse(product));
        }
        return responses;
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .customerName(order.getUser().getFullName())
                .customerEmail(order.getUser().getEmail())
                .subtotal(order.getSubtotal())
                .shipping(order.getShipping())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .itemCount(order.getItems() != null ? order.getItems().size() : 0)
                .orderDate(order.getOrderDate())
                .deliveredDate(order.getDeliveredDate())
                .createdAt(order.getCreatedAt())
                .build();
    }
    
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .stockQuantity(product.getStockQuantity())
                .inStock(product.getInStock())
                .thumbnail(product.getThumbnail())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
