package com.example.shopingdiscount.controller;

import com.example.shopingdiscount.model.DiscountRequest;
import com.example.shopingdiscount.model.DiscountResponse;
import com.example.shopingdiscount.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for the discount module so external clients (e.g. Bruno, Postman) can
 * calculate the final price of an order.
 */
@RestController
@RequestMapping("/api/discount")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<DiscountResponse> calculate(@Valid @RequestBody DiscountRequest request) {
        return ResponseEntity.ok(discountService.calculate(request));
    }
}
