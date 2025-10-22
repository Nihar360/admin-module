package com.ecommerce.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private Long id;
    private String name;
    private String sku;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    
    private Long categoryId;
    private String categoryName;
    
    private Integer stockQuantity;
    private Boolean inStock;
    private String thumbnail;
    private Boolean isActive;
    
    private String metaDescription;
    private String metaKeywords;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
