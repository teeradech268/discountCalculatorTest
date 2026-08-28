package com.example.shopingdiscount.model;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request payload for a single discount campaign. Only the fields relevant to
 * {@link #type()} need to be supplied - see {@link CampaignRequestType} for which
 * fields each type requires. Unused fields may be omitted/null.
 *
 * Example (fixed amount coupon): {"type": "FIXED_AMOUNT_COUPON", "amount": 50}
 * Example (category on-top):     {"type": "CATEGORY_PERCENTAGE_ON_TOP", "category": "CLOTHING", "percentage": 15}
 * Example (seasonal):            {"type": "SEASONAL", "every": 300, "discount": 40}
 */
public record CampaignRequest(

        @NotNull(message = "type is required, one of: FIXED_AMOUNT_COUPON, PERCENTAGE_COUPON, "
                + "CATEGORY_PERCENTAGE_ON_TOP, POINTS_ON_TOP, SEASONAL")
        CampaignRequestType type,

        BigDecimal amount,
        BigDecimal percentage,
        ItemCategory category,
        BigDecimal points,
        BigDecimal every,
        BigDecimal discount
) {
}
