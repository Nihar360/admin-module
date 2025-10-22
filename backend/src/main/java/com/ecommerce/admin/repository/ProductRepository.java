package com.ecommerce.admin.repository;

import com.ecommerce.admin.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySku(String sku);
    
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findByStockQuantityLessThan(Integer quantity);
    
    List<Product> findByStockQuantity(Integer quantity);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:search IS NULL OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:inStock IS NULL OR p.inStock = :inStock)")
    Page<Product> findByFilters(
        @Param("categoryId") Long categoryId,
        @Param("search") String search,
        @Param("inStock") Boolean inStock,
        Pageable pageable
    );
    
    Long countByInStock(Boolean inStock);
}
