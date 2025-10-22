package com.ecommerce.admin.service;

import com.ecommerce.admin.dto.request.ProductCreateRequest;
import com.ecommerce.admin.dto.request.ProductUpdateRequest;
import com.ecommerce.admin.dto.request.StockAdjustmentRequest;
import com.ecommerce.admin.dto.response.PageResponse;
import com.ecommerce.admin.dto.response.ProductResponse;
import com.ecommerce.admin.exception.BadRequestException;
import com.ecommerce.admin.exception.ResourceNotFoundException;
import com.ecommerce.admin.exception.ValidationException;
import com.ecommerce.admin.model.Category;
import com.ecommerce.admin.model.Product;
import com.ecommerce.admin.repository.CategoryRepository;
import com.ecommerce.admin.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProducts(
            Long categoryId,
            String search,
            Boolean inStock,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findByFilters(categoryId, search, inStock, pageable);
        
        List<ProductResponse> responses = new ArrayList<>();
        for (Product product : productPage.getContent()) {
            responses.add(mapToProductResponse(product));
        }
        
        log.info("Retrieved {} products with categoryId={}, search={}, inStock={}", 
                productPage.getTotalElements(), categoryId, search, inStock);
        
        return PageResponse.<ProductResponse>builder()
                .content(responses)
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .currentPage(productPage.getNumber())
                .pageSize(productPage.getSize())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        log.info("Retrieved product: {}", product.getName());
        
        return mapToProductResponse(product);
    }
    
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        if (request.getSku() != null && productRepository.findBySku(request.getSku()).isPresent()) {
            throw new ValidationException("Product with SKU " + request.getSku() + " already exists");
        }
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        String sku = request.getSku() != null ? request.getSku() : generateSku();
        
        Product product = Product.builder()
                .name(request.getName())
                .sku(sku)
                .description(request.getDescription())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .category(category)
                .stockQuantity(request.getStockQuantity())
                .thumbnail(request.getThumbnail())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .metaDescription(request.getMetaDescription())
                .metaKeywords(request.getMetaKeywords())
                .build();
        
        product = productRepository.save(product);
        
        log.info("Created new product: {} with SKU: {}", product.getName(), product.getSku());
        
        return mapToProductResponse(product);
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        
        if (request.getDiscountPrice() != null) {
            product.setDiscountPrice(request.getDiscountPrice());
        }
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        
        if (request.getThumbnail() != null) {
            product.setThumbnail(request.getThumbnail());
        }
        
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }
        
        if (request.getMetaDescription() != null) {
            product.setMetaDescription(request.getMetaDescription());
        }
        
        if (request.getMetaKeywords() != null) {
            product.setMetaKeywords(request.getMetaKeywords());
        }
        
        product = productRepository.save(product);
        
        log.info("Updated product: {}", product.getName());
        
        return mapToProductResponse(product);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        product.setIsActive(false);
        productRepository.save(product);
        
        log.info("Soft deleted product: {}", product.getName());
    }
    
    @Transactional
    public ProductResponse adjustStock(Long id, StockAdjustmentRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        int currentStock = product.getStockQuantity();
        int newStock;
        
        if ("add".equalsIgnoreCase(request.getType())) {
            newStock = currentStock + request.getQuantity();
            log.info("Adding {} units to product: {}, current: {}, new: {}", 
                    request.getQuantity(), product.getName(), currentStock, newStock);
        } else if ("remove".equalsIgnoreCase(request.getType())) {
            newStock = currentStock - request.getQuantity();
            if (newStock < 0) {
                throw new BadRequestException("Cannot remove more stock than available. Current stock: " + currentStock);
            }
            log.info("Removing {} units from product: {}, current: {}, new: {}", 
                    request.getQuantity(), product.getName(), currentStock, newStock);
        } else {
            throw new ValidationException("Invalid adjustment type. Use 'add' or 'remove'");
        }
        
        product.setStockQuantity(newStock);
        product = productRepository.save(product);
        
        return mapToProductResponse(product);
    }
    
    private String generateSku() {
        return "PRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts() {
        log.info("Fetching low stock products (stock < 10)");
        List<Product> lowStockProducts = productRepository.findByStockQuantityLessThan(10);
        
        List<ProductResponse> responses = new ArrayList<>();
        for (Product product : lowStockProducts) {
            responses.add(mapToProductResponse(product));
        }
        
        log.info("Found {} low stock products", responses.size());
        return responses;
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
                .metaDescription(product.getMetaDescription())
                .metaKeywords(product.getMetaKeywords())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
