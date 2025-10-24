package com.ecommerce.admin.config;

import com.ecommerce.admin.model.enums.OrderStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Custom converter for case-insensitive OrderStatus enum conversion
 * Allows API to accept both "pending" and "PENDING" as valid input
 */
@Component
public class StringToOrderStatusConverter implements Converter<String, OrderStatus> {
    
    @Override
    public OrderStatus convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        
        try {
            return OrderStatus.valueOf(source.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                String.format("Invalid order status: '%s'. Valid values are: %s", 
                    source, getValidStatusesString())
            );
        }
    }
    
    private String getValidStatusesString() {
        return String.join(", ", 
            java.util.Arrays.stream(OrderStatus.values())
                .map(Enum::name)
                .toArray(String[]::new)
        );
    }
}