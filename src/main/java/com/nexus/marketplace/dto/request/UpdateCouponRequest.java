package com.nexus.marketplace.dto.request;

import com.nexus.marketplace.enums.ValueType;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateCouponRequest {

    private String name;
    private String description;
    private String imageUrl;

    @DecimalMin(value = "0.0", message = "Cost price must be >= 0")
    private BigDecimal costPrice;

    @DecimalMin(value = "0.0", message = "Margin percentage must be >= 0")
    private BigDecimal marginPercentage;

    private ValueType valueType;
    private String couponValue;
}
