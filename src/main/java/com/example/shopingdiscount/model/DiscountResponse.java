package com.example.shopingdiscount.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response payload for POST /api/discount/calculate.
 * Echoes back the items and campaigns that were submitted, plus the calculated
 * {@code totalPrice}.
 */
public record DiscountResponse(
        List<CartItemRequest> items,
        List<CampaignRequest> campaigns,
        BigDecimal totalPrice
) {
}
