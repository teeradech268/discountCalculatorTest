package com.example.shopingdiscount.service;

import com.example.shopingdiscount.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * A discount campaign that can be applied to a shopping cart order.
 *
 * <p>Implementations must be stateless/immutable and side-effect free: given the same
 * running total and cart items they must always compute the same discount amount.
 */
public interface DiscountCampaign {

    /** The category this campaign belongs to. Only one campaign per category may be active at once. */
    CampaignType getType();

    /**
     * Calculates the discount amount this campaign contributes.
     *
     * @param currentTotal the running total price of the order, after any previously
     *                      applied campaigns (campaigns are applied in COUPON -&gt; ON_TOP -&gt; SEASONAL order)
     * @param items         the original, undiscounted cart items - used by campaigns that need
     *                      to inspect item categories or original subtotals (e.g. category on-top discounts)
     * @return the discount amount to subtract from {@code currentTotal}; never negative and
     *         never greater than {@code currentTotal}
     */
    BigDecimal calculateDiscount(BigDecimal currentTotal, List<CartItem> items);

    /** Human readable name, useful for logging/debugging purposes. */
    String getDescription();
}
