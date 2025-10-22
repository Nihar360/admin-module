package com.ecommerce.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String thumbnail;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal total;
    private String size;
    private String color;
}
