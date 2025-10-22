package com.ecommerce.admin.service;

import com.ecommerce.admin.exception.BadRequestException;
import com.ecommerce.admin.exception.ResourceNotFoundException;
import com.ecommerce.admin.model.Category;
import com.ecommerce.admin.repository.CategoryRepository;
import com.ecommerce.admin.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Cacheable(value = "categories", key = "'all'")
    public List<Category> getAllCategories() {
        log.debug("Fetching all categories");
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        log.debug("Fetching category with id: {}", id);
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public Category createCategory(Category category) {
        log.info("Creating new category: {}", category.getName());
        
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BadRequestException("Category name cannot be empty");
        }

        if (categoryRepository.existsByName(category.getName())) {
            throw new BadRequestException("Category with name '" + category.getName() + "' already exists");
        }

        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        Category saved = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public Category updateCategory(Long id, Category categoryDetails) {
        log.info("Updating category with id: {}", id);
        
        Category category = getCategoryById(id);

        if (categoryDetails.getName() != null && !categoryDetails.getName().isBlank()) {
            if (!category.getName().equals(categoryDetails.getName()) &&
                categoryRepository.existsByName(categoryDetails.getName())) {
                throw new BadRequestException("Category with name '" + categoryDetails.getName() + "' already exists");
            }
            category.setName(categoryDetails.getName());
        }

        if (categoryDetails.getDescription() != null) {
            category.setDescription(categoryDetails.getDescription());
        }

        if (categoryDetails.getImage() != null) {
            category.setImage(categoryDetails.getImage());
        }

        category.setUpdatedAt(LocalDateTime.now());

        Category updated = categoryRepository.save(category);
        log.info("Category updated successfully: {}", updated.getId());
        return updated;
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);
        
        Category category = getCategoryById(id);

        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new BadRequestException(
                "Cannot delete category with " + productCount + " products. " +
                "Please reassign or delete the products first."
            );
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully: {}", id);
    }

    public Long getProductCountByCategory(Long categoryId) {
        log.debug("Getting product count for category: {}", categoryId);
        getCategoryById(categoryId);
        return productRepository.countByCategoryId(categoryId);
    }

    @Cacheable(value = "categories", key = "'active'")
    public List<Category> getActiveCategories() {
        log.debug("Fetching active categories");
        return categoryRepository.findAll();
    }
}
