package com.nexus.marketplace.dto.response;

import com.nexus.marketplace.entity.Coupon;
import com.nexus.marketplace.enums.ValueType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


// Full admin view: includes cost_price, margin_percentage, sold status, and coupon value.

@Getter
public class AdminProductResponse {

    private final UUID id;
    private final String name;
    private final String description;
    private final String imageUrl;
    private final BigDecimal costPrice;
    private final BigDecimal marginPercentage;
    private final BigDecimal minimumSellPrice;
    private final boolean sold;
    private final ValueType valueType;
    private final String couponValue;
    private final Instant createdAt;
    private final Instant updatedAt;

    public AdminProductResponse(Coupon coupon) {
        this.id = coupon.getId();
        this.name = coupon.getName();
        this.description = coupon.getDescription();
        this.imageUrl = coupon.getImageUrl();
        this.costPrice = coupon.getCostPrice();
        this.marginPercentage = coupon.getMarginPercentage();
        this.minimumSellPrice = coupon.getMinimumSellPrice();
        this.sold = coupon.isSold();
        this.valueType = coupon.getValueType();
        this.couponValue = coupon.getCouponValue();
        this.createdAt = coupon.getCreatedAt();
        this.updatedAt = coupon.getUpdatedAt();
    }
}
