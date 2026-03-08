package com.nexus.marketplace.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexus.marketplace.enums.ValueType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateCouponRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.0", message = "Cost price must be >= 0")
    private BigDecimal costPrice;

    @NotNull(message = "Margin percentage is required")
    @DecimalMin(value = "0.0", message = "Margin percentage must be >= 0")
    private BigDecimal marginPercentage;

    @NotNull(message = "Value type is required")
    private ValueType valueType;

    @NotBlank(message = "Coupon value is required")
    private String couponValue;
}
