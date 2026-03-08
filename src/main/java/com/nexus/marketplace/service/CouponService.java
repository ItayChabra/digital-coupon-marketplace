package com.nexus.marketplace.service;

import com.nexus.marketplace.dto.request.CreateCouponRequest;
import com.nexus.marketplace.dto.request.ResellerPurchaseRequest;
import com.nexus.marketplace.dto.request.UpdateCouponRequest;
import com.nexus.marketplace.dto.response.AdminProductResponse;
import com.nexus.marketplace.dto.response.ProductResponse;
import com.nexus.marketplace.dto.response.PurchaseResponse;
import com.nexus.marketplace.entity.Coupon;
import com.nexus.marketplace.exception.ProductAlreadySoldException;
import com.nexus.marketplace.exception.ProductNotFoundException;
import com.nexus.marketplace.exception.ResellerPriceTooLowException;
import com.nexus.marketplace.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    // ── Admin ────────────────────────────────────────────────────────────────

    public AdminProductResponse createCoupon(CreateCouponRequest req) {
        Coupon coupon = new Coupon();
        coupon.setName(req.getName());
        coupon.setDescription(req.getDescription());
        coupon.setImageUrl(req.getImageUrl());
        coupon.setCostPrice(req.getCostPrice());
        coupon.setMarginPercentage(req.getMarginPercentage());
        coupon.setValueType(req.getValueType());
        coupon.setCouponValue(req.getCouponValue());
        // @PrePersist at coupon entity will calculate minimumSellPrice before insert
        return new AdminProductResponse(couponRepository.save(coupon));
    }

    public List<AdminProductResponse> getAllCouponsAdmin() {
        return couponRepository.findAll().stream()
                .map(AdminProductResponse::new)
                .toList();
    }

    public AdminProductResponse getCouponAdmin(UUID id) {
        return couponRepository.findById(id)
                .map(AdminProductResponse::new)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
    }

    public AdminProductResponse updateCoupon(UUID id, UpdateCouponRequest req) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));

        if (req.getName() != null) coupon.setName(req.getName());
        if (req.getDescription() != null) coupon.setDescription(req.getDescription());
        if (req.getImageUrl() != null) coupon.setImageUrl(req.getImageUrl());
        if (req.getCostPrice() != null) coupon.setCostPrice(req.getCostPrice());
        if (req.getMarginPercentage() != null) coupon.setMarginPercentage(req.getMarginPercentage());
        if (req.getValueType() != null) coupon.setValueType(req.getValueType());
        if (req.getCouponValue() != null) coupon.setCouponValue(req.getCouponValue());
        // @PreUpdate at coupon entity recalculates minimumSellPrice if cost/margin changed

        return new AdminProductResponse(couponRepository.save(coupon));
    }

    public void deleteCoupon(UUID id) {
        if (!couponRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found: " + id);
        }
        couponRepository.deleteById(id);
    }

    // ── Public / Reseller ────────────────────────────────────────────────────

    public List<ProductResponse> getAvailableProducts() {
        return couponRepository.findAllBySoldFalse().stream()
                .map(ProductResponse::new)
                .toList();
    }

    public ProductResponse getProductById(UUID id) {
        return couponRepository.findById(id)
                .map(ProductResponse::new)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
    }


// Reseller purchase: validates reseller_price >= minimum_sell_price,
// then atomically marks the coupon as sold.

    @Transactional
    public PurchaseResponse resellerPurchase(UUID id, ResellerPurchaseRequest req) {
        Coupon coupon = couponRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));

        if (coupon.isSold()) {
            throw new ProductAlreadySoldException("Product is already sold: " + id);
        }

        if (req.getResellerPrice().compareTo(coupon.getMinimumSellPrice()) < 0) {
            throw new ResellerPriceTooLowException(
                    "reseller_price must be >= " + coupon.getMinimumSellPrice()
            );
        }

        coupon.setSold(true);
        couponRepository.save(coupon);

        return new PurchaseResponse(
                coupon.getId(),
                req.getResellerPrice(),
                coupon.getValueType(),
                coupon.getCouponValue()
        );
    }


// Customer (frontend) purchase: price is fixed at minimum_sell_price,
// customer cannot override it.

    @Transactional
    public PurchaseResponse customerPurchase(UUID id) {
        Coupon coupon = couponRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));

        if (coupon.isSold()) {
            throw new ProductAlreadySoldException("Product is already sold: " + id);
        }

        coupon.setSold(true);
        couponRepository.save(coupon);

        return new PurchaseResponse(
                coupon.getId(),
                coupon.getMinimumSellPrice(),
                coupon.getValueType(),
                coupon.getCouponValue()
        );
    }
}
