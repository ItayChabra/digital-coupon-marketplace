package com.nexus.marketplace.dto.response;

import com.nexus.marketplace.entity.Coupon;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

// Safe public view of a product. never exposes cost_price or margin_percentage.
// Price shown is the minimum_sell_price (what the customer/reseller pays at minimum).

@Getter
public class ProductResponse {

    private final UUID id;
    private final String name;
    private final String description;
    private final String imageUrl;
    private final BigDecimal price;

    public ProductResponse(Coupon coupon) {
        this.id = coupon.getId();
        this.name = coupon.getName();
        this.description = coupon.getDescription();
        this.imageUrl = coupon.getImageUrl();
        this.price = coupon.getMinimumSellPrice();
    }
}
