package com.ecommerce.admin.util;

import com.ecommerce.admin.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );
    
    private static final Pattern COUPON_CODE_PATTERN = Pattern.compile(
        "^[A-Z0-9]{4,20}$"
    );
    
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private ValidationUtil() {
    }

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Invalid email format");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password cannot be empty");
        }
        if (password.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }
        if (password.length() > 100) {
            throw new BadRequestException("Password must not exceed 100 characters");
        }
    }

    public static void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return;
        }
        if (!PHONE_PATTERN.matcher(phone.replaceAll("[\\s-]", "")).matches()) {
            throw new BadRequestException("Invalid phone number format");
        }
    }

    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(fieldName + " cannot be null");
        }
    }

    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(fieldName + " cannot be empty");
        }
    }

    public static void validatePositive(Number value, String fieldName) {
        validateNotNull(value, fieldName);
        if (value.doubleValue() <= 0) {
            throw new BadRequestException(fieldName + " must be positive");
        }
    }

    public static void validateNonNegative(Number value, String fieldName) {
        validateNotNull(value, fieldName);
        if (value.doubleValue() < 0) {
            throw new BadRequestException(fieldName + " must be non-negative");
        }
    }

    public static void validateRange(Number value, Number min, Number max, String fieldName) {
        validateNotNull(value, fieldName);
        double val = value.doubleValue();
        double minVal = min.doubleValue();
        double maxVal = max.doubleValue();
        
        if (val < minVal || val > maxVal) {
            throw new BadRequestException(
                String.format("%s must be between %s and %s", fieldName, min, max)
            );
        }
    }

    public static void validateStringLength(String value, int minLength, int maxLength, String fieldName) {
        validateNotBlank(value, fieldName);
        int length = value.length();
        
        if (length < minLength || length > maxLength) {
            throw new BadRequestException(
                String.format("%s length must be between %d and %d characters", 
                    fieldName, minLength, maxLength)
            );
        }
    }

    public static void validateCouponCode(String code) {
        validateNotBlank(code, "Coupon code");
        if (!COUPON_CODE_PATTERN.matcher(code).matches()) {
            throw new BadRequestException(
                "Coupon code must be 4-20 characters long and contain only uppercase letters and numbers"
            );
        }
    }

    public static void validatePrice(BigDecimal price, String fieldName) {
        validateNotNull(price, fieldName);
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(fieldName + " cannot be negative");
        }
        if (price.scale() > 2) {
            throw new BadRequestException(fieldName + " can have at most 2 decimal places");
        }
    }

    public static void validateDiscount(BigDecimal discount, BigDecimal price) {
        validatePrice(discount, "Discount");
        if (discount.compareTo(price) > 0) {
            throw new BadRequestException("Discount cannot be greater than price");
        }
    }

    public static void validatePercentage(BigDecimal percentage, String fieldName) {
        validateNotNull(percentage, fieldName);
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || 
            percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BadRequestException(fieldName + " must be between 0 and 100");
        }
    }

    public static void validateStockQuantity(Integer quantity) {
        validateNotNull(quantity, "Stock quantity");
        if (quantity < 0) {
            throw new BadRequestException("Stock quantity cannot be negative");
        }
    }

    public static Pageable createPageable(Integer page, Integer size) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        return PageRequest.of(pageNumber, pageSize);
    }

    public static Pageable createPageable(Integer page, Integer size, Sort sort) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    public static void validatePageNumber(Integer page) {
        if (page != null && page < 0) {
            throw new BadRequestException("Page number cannot be negative");
        }
    }

    public static void validatePageSize(Integer size) {
        if (size != null && (size <= 0 || size > MAX_PAGE_SIZE)) {
            throw new BadRequestException(
                String.format("Page size must be between 1 and %d", MAX_PAGE_SIZE)
            );
        }
    }

    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    public static void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid " + fieldName);
        }
    }
}
