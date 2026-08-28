package com.example.shopingdiscount.service;

import com.example.shopingdiscount.common.CommonConstant;
import com.example.shopingdiscount.config.handler.InvalidDiscountConfigurationException;
import com.example.shopingdiscount.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Coupon campaign that discounts the entire cart by subtracting a percentage
 * from the total price.
 */
public final class PercentageCouponCampaign implements DiscountCampaign {

    private final BigDecimal percentage;

    public PercentageCouponCampaign(BigDecimal percentage) {
        if (percentage == null || percentage.signum() < 0 || percentage.compareTo(CommonConstant.HUNDRED) > 0) {
            throw new InvalidDiscountConfigurationException("Percentage coupon discount must be between 0 and 100");
        }
        this.percentage = percentage;
    }

    @Override
    public CampaignType getType() {
        return CampaignType.COUPON;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal currentTotal, List<CartItem> items) {
        return currentTotal.multiply(percentage).divide(CommonConstant.HUNDRED);
    }

    @Override
    public String getDescription() {
        return "Percentage coupon: -" + percentage + "%";
    }
}
