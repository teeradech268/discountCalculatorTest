package com.example.shopingdiscount.service;

import com.example.shopingdiscount.config.handler.InvalidDiscountConfigurationException;
import com.example.shopingdiscount.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seasonal campaign: from the (running) total price, at every X THB, subtract a fixed
 * discount amount Y THB. E.g. "40 THB off at every 300 THB" on a 750 THB order gives
 * floor(750 / 300) = 2 discount steps = 80 THB off.
 */
public final class SeasonalCampaign implements DiscountCampaign {

    private final BigDecimal every;
    private final BigDecimal discount;

    public SeasonalCampaign(BigDecimal every, BigDecimal discount) {
        if (every == null || every.signum() <= 0) {
            throw new InvalidDiscountConfigurationException("Seasonal campaign 'every X THB' must be positive");
        }
        if (discount == null || discount.signum() < 0) {
            throw new InvalidDiscountConfigurationException("Seasonal campaign discount amount must not be negative");
        }
        this.every = every;
        this.discount = discount;
    }

    @Override
    public CampaignType getType() {
        return CampaignType.SEASONAL;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal currentTotal, List<CartItem> items) {
        BigDecimal steps = currentTotal.divideToIntegralValue(every);
        BigDecimal totalDiscount = steps.multiply(discount);
        return totalDiscount.min(currentTotal);
    }

    @Override
    public String getDescription() {
        return "Seasonal: -" + discount + " THB at every " + every + " THB";
    }
}
