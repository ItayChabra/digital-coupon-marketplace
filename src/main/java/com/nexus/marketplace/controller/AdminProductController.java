package com.nexus.marketplace.controller;

import com.nexus.marketplace.dto.request.CreateCouponRequest;
import com.nexus.marketplace.dto.request.UpdateCouponRequest;
import com.nexus.marketplace.dto.response.AdminProductResponse;
import com.nexus.marketplace.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<AdminProductResponse> createCoupon(@Valid @RequestBody CreateCouponRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.createCoupon(req));
    }

    @GetMapping
    public ResponseEntity<List<AdminProductResponse>> getAllProducts() {
        return ResponseEntity.ok(couponService.getAllCouponsAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminProductResponse> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(couponService.getCouponAdmin(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminProductResponse> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCouponRequest req) {
        return ResponseEntity.ok(couponService.updateCoupon(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
