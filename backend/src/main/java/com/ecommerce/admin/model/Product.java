package com.ecommerce.admin.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 100, unique = true)
    private String sku;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(length = 100)
    private String categoryName;
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;
    
    @Column(name = "stock_count", nullable = false)
    private Integer stockCount = 0;
    
    @Column(name = "in_stock", nullable = false)
    private Boolean inStock = true;
    
    @Column(length = 500)
    private String thumbnail;
    
    @Column(length = 500)
    private String image;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(length = 50)
    private String badge;
    
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    @Column(nullable = false)
    private Double rating = 0.0;
    
    @Column(nullable = false)
    private Integer reviews = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "meta_description", length = 500)
    private String metaDescription;
    
    @Column(name = "meta_keywords", length = 255)
    private String metaKeywords;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
        if (isActive == null) {
            isActive = true;
        }
        updateInStock();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateInStock();
    }
    
    private void updateInStock() {
        inStock = stockQuantity != null && stockQuantity > 0;
    }
}
