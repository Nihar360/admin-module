package com.ecommerce.admin.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFeature {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(length = 255)
    private String feature;
}
