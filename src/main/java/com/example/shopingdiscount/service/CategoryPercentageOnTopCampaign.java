package com.example.shopingdiscount.service;

import com.example.shopingdiscount.common.CommonConstant;
import com.example.shopingdiscount.config.handler.InvalidDiscountConfigurationException;
import com.example.shopingdiscount.model.CartItem;
import com.example.shopingdiscount.model.ItemCategory;

import java.math.BigDecimal;
import java.util.List;

/**
 * On Top campaign that discounts a percentage off the subtotal of a specific item category
 * (e.g. 15% off all Clothing items), regardless of any coupon already applied.
 */
public final class CategoryPercentageOnTopCampaign implements DiscountCampaign {

    private final ItemCategory category;
    private final BigDecimal percentage;

    public CategoryPercentageOnTopCampaign(ItemCategory category, BigDecimal percentage) {
        if (category == null) {
            throw new InvalidDiscountConfigurationException("Category on-top campaign requires a category");
        }
        if (percentage == null || percentage.signum() < 0 || percentage.compareTo(CommonConstant.HUNDRED) > 0) {
            throw new InvalidDiscountConfigurationException("Category on-top discount must be between 0 and 100");
        }
        this.category = category;
        this.percentage = percentage;
    }

    @Override
    public CampaignType getType() {
        return CampaignType.ON_TOP;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal currentTotal, List<CartItem> items) {
        BigDecimal categorySubtotal = items.stream()
                .filter(item -> item.getCategory() == category)
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = categorySubtotal.multiply(percentage).divide(CommonConstant.HUNDRED);
        return discount.min(currentTotal);
    }

    @Override
    public String getDescription() {
        return "On top " + percentage + "% off " + category;
    }
}
