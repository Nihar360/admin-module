package com.ecommerce.admin.controller;

import com.ecommerce.admin.dto.request.ProductCreateRequest;
import com.ecommerce.admin.dto.request.ProductUpdateRequest;
import com.ecommerce.admin.dto.request.StockAdjustmentRequest;
import com.ecommerce.admin.dto.response.ApiResponse;
import com.ecommerce.admin.dto.response.PageResponse;
import com.ecommerce.admin.dto.response.ProductResponse;
import com.ecommerce.admin.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching products with categoryId={}, search={}, inStock={}, page={}, size={}", 
                categoryId, search, inStock, page, size);
        PageResponse<ProductResponse> products = productService.getProducts(categoryId, search, inStock, page, size);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProduct(@PathVariable Long id) {
        log.info("Fetching product with id: {}", id);
        ProductResponse product = productService.getProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", product));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        log.info("Creating new product: {}", request.getName());
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", product));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        log.info("Updating product with id: {}", id);
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with id: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse> adjustStock(
            @PathVariable Long id,
            @Valid @RequestBody StockAdjustmentRequest request) {
        log.info("Adjusting stock for product {}: type={}, quantity={}", id, request.getType(), request.getQuantity());
        ProductResponse product = productService.adjustStock(id, request);
        return ResponseEntity.ok(ApiResponse.success("Stock adjusted successfully", product));
    }
}
