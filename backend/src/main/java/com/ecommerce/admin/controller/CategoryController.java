package com.ecommerce.admin.controller;

import com.ecommerce.admin.aspect.AdminActivityAspect.LogActivity;
import com.ecommerce.admin.dto.response.ApiResponse;
import com.ecommerce.admin.model.Category;
import com.ecommerce.admin.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "APIs for managing product categories")
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve a list of all product categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve a specific category by its ID")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @LogActivity(action = "CREATE_CATEGORY", resourceType = "CATEGORY")
    @Operation(summary = "Create new category", description = "Create a new product category")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.<Category>builder()
                .success(true)
                .message("Category created successfully")
                .data(created)
                .build());
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_CATEGORY", resourceType = "CATEGORY")
    @Operation(summary = "Update category", description = "Update an existing product category")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category category) {
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(ApiResponse.<Category>builder()
            .success(true)
            .message("Category updated successfully")
            .data(updated)
            .build());
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_CATEGORY", resourceType = "CATEGORY")
    @Operation(summary = "Delete category", description = "Delete a product category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
            .success(true)
            .message("Category deleted successfully")
            .build());
    }

    @GetMapping("/{id}/products/count")
    @Operation(summary = "Get product count for category", description = "Get the number of products in a category")
    public ResponseEntity<ApiResponse<Long>> getProductCountByCategory(@PathVariable Long id) {
        Long count = categoryService.getProductCountByCategory(id);
        return ResponseEntity.ok(ApiResponse.<Long>builder()
            .success(true)
            .data(count)
            .build());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active categories", description = "Retrieve all active product categories")
    public ResponseEntity<List<Category>> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }
}
