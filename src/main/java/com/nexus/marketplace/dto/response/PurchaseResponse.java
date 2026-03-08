package com.nexus.marketplace.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexus.marketplace.enums.ValueType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PurchaseResponse {
    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("final_price")
    private BigDecimal finalPrice;

    @JsonProperty("value_type")
    private ValueType valueType;

    private String value;
}
