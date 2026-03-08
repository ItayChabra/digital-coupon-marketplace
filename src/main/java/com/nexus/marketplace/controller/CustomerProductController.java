package com.nexus.marketplace.controller;

import com.nexus.marketplace.dto.response.ProductResponse;
import com.nexus.marketplace.dto.response.PurchaseResponse;
import com.nexus.marketplace.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer/products")
@RequiredArgsConstructor
public class CustomerProductController {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAvailableProducts() {
        return ResponseEntity.ok(couponService.getAvailableProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(couponService.getProductById(productId));
    }


// No request body: price is always fixed at minimum_sell_price server-side.
// Customers cannot influence the price.

    @PostMapping("/{productId}/purchase")
    public ResponseEntity<PurchaseResponse> purchase(@PathVariable UUID productId) {
        return ResponseEntity.ok(couponService.customerPurchase(productId));
    }
}
