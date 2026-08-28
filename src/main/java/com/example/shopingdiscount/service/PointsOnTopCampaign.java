package com.example.shopingdiscount.service;

import com.example.shopingdiscount.common.CommonConstant;
import com.example.shopingdiscount.config.handler.InvalidDiscountConfigurationException;
import com.example.shopingdiscount.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * On Top campaign where customers spend loyalty points for a fixed discount amount
 * (1 point = 1 THB). The discount is capped at 20% of the total price - this cap
 * percentage is a fixed business rule, not a configurable parameter.
 */
public final class PointsOnTopCampaign implements DiscountCampaign {

    private final BigDecimal points;

    public PointsOnTopCampaign(BigDecimal points) {
        if (points == null || points.signum() < 0) {
            throw new InvalidDiscountConfigurationException("Points must not be negative");
        }
        this.points = points;
    }

    @Override
    public CampaignType getType() {
        return CampaignType.ON_TOP;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal currentTotal, List<CartItem> items) {
        BigDecimal cap = currentTotal.multiply(CommonConstant.POINTS_ON_TOP_CAP_PERCENTAGE).divide(CommonConstant.HUNDRED);
        return points.min(cap).min(currentTotal);
    }

    @Override
    public String getDescription() {
        return "On top points redemption: " + points + " points (capped at 20% of total)";
    }
}
