package com.example.shopingdiscount.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request payload for POST /api/discount/calculate.
 */
public record DiscountRequest(

        @NotEmpty(message = "items must not be empty")
        @Valid
        List<CartItemRequest> items,

        @Valid
        List<CampaignRequest> campaigns
) {
}
