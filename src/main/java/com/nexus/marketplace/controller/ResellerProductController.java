package com.nexus.marketplace.controller;

import com.nexus.marketplace.dto.request.ResellerPurchaseRequest;
import com.nexus.marketplace.dto.response.ProductResponse;
import com.nexus.marketplace.dto.response.PurchaseResponse;
import com.nexus.marketplace.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ResellerProductController {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAvailableProducts() {
        return ResponseEntity.ok(couponService.getAvailableProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(couponService.getProductById(productId));
    }

    @PostMapping("/{productId}/purchase")
    public ResponseEntity<PurchaseResponse> purchase(
            @PathVariable UUID productId,
            @Valid @RequestBody ResellerPurchaseRequest req) {
        return ResponseEntity.ok(couponService.resellerPurchase(productId, req));
    }
}
