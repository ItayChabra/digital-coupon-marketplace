package com.nexus.marketplace.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ResellerPurchaseRequest {

    @NotNull(message = "reseller_price is required")
    @DecimalMin(value = "0.01", message = "reseller_price must be positive")
    private BigDecimal resellerPrice;
}
