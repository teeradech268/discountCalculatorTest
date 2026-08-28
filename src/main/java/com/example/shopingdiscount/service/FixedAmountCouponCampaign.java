package com.example.shopingdiscount.service;

import com.example.shopingdiscount.config.handler.InvalidDiscountConfigurationException;
import com.example.shopingdiscount.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Coupon campaign that discounts the entire cart by subtracting a fixed amount
 * from the total price.
 */
public final class FixedAmountCouponCampaign implements DiscountCampaign {

    private final BigDecimal amount;

    public FixedAmountCouponCampaign(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new InvalidDiscountConfigurationException("Fixed amount coupon discount must not be negative");
        }
        this.amount = amount;
    }

    @Override
    public CampaignType getType() {
        return CampaignType.COUPON;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal currentTotal, List<CartItem> items) {
        return amount.min(currentTotal);
    }

    @Override
    public String getDescription() {
        return "Fixed amount coupon: -" + amount + " THB";
    }
}
