package com.ecommerce.admin.controller;

import com.ecommerce.admin.dto.response.ApiResponse;
import com.ecommerce.admin.dto.response.PageResponse;
import com.ecommerce.admin.dto.response.ProductResponse;
import com.ecommerce.admin.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
@Slf4j
@Validated
public class InventoryController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getInventory(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching inventory with categoryId={}, search={}, page={}, size={}", 
                categoryId, search, page, size);
        PageResponse<ProductResponse> inventory = productService.getProducts(categoryId, search, null, page, size);
        return ResponseEntity.ok(ApiResponse.success("Inventory retrieved successfully", inventory));
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse> getLowStockProducts() {
        log.info("Fetching low stock products");
        List<ProductResponse> lowStockProducts = productService.getLowStockProducts();
        return ResponseEntity.ok(ApiResponse.success("Low stock products retrieved successfully", lowStockProducts));
    }
}
