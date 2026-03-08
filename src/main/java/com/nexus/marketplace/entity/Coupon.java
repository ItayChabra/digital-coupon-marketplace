package com.nexus.marketplace.entity;

import com.nexus.marketplace.enums.ValueType;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "coupons")
@DiscriminatorValue("COUPON")
@PrimaryKeyJoinColumn(name = "product_id")
@Getter
@Setter
@NoArgsConstructor
public class Coupon extends Product {

    @PositiveOrZero
    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    @PositiveOrZero
    @Column(name = "margin_percentage", nullable = false, precision = 10, scale = 2)
    private BigDecimal marginPercentage;

    // Stored for query efficiency; always recalculated on save
    @Column(name = "minimum_sell_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal minimumSellPrice;

    @Column(name = "is_sold", nullable = false)
    private boolean sold = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false)
    private ValueType valueType;

    // The actual redeemable value (barcode string or image URL) — never exposed until purchased
    @Column(name = "coupon_value", columnDefinition = "TEXT", nullable = false)
    private String couponValue;

    @PrePersist
    @PreUpdate
    public void calculateMinimumSellPrice() {
        if (costPrice != null && marginPercentage != null) {
            // minimum_sell_price = cost_price * (1 + margin_percentage / 100)
            BigDecimal factor = BigDecimal.ONE.add(
                    marginPercentage.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
            );
            this.minimumSellPrice = costPrice.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        }
    }
}
