package com.ecommerce.admin.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CouponType {
    @JsonProperty("percentage")
    PERCENTAGE,
    
    @JsonProperty("fixed")
    FIXED
}