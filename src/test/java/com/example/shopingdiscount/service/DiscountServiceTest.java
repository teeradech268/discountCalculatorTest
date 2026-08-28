package com.example.shopingdiscount.service;

import com.example.shopingdiscount.config.handler.InvalidDiscountConfigurationException;
import com.example.shopingdiscount.model.CartItem;
import com.example.shopingdiscount.model.ItemCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiscountServiceTest {

    private final DiscountService calculator = new DiscountService();

    // ---- Examples taken directly from the assignment PDF ----

    @Test
    void fixedAmountCoupon_matchesPdfExample() {
        List<CartItem> items = List.of(
                new CartItem("T-Shirt", ItemCategory.CLOTHING, new BigDecimal("350")),
                new CartItem("Hat", ItemCategory.ACCESSORIES, new BigDecimal("250"))
        );
        List<DiscountCampaign> campaigns = List.of(new FixedAmountCouponCampaign(new BigDecimal("50")));

        BigDecimal result = calculator.calculateFinalPrice(items, campaigns);

        assertThat(result).isEqualByComparingTo("550");
    }

    @Test
    void percentageCoupon_matchesPdfExample() {
        List<CartItem> items = List.of(
                new CartItem("T-Shirt", ItemCategory.CLOTHING, new BigDecimal("350")),
                new CartItem("Hat", ItemCategory.ACCESSORIES, new BigDecimal("250"))
        );
        List<DiscountCampaign> campaigns = List.of(new PercentageCouponCampaign(new BigDecimal("10")));

        BigDecimal result = calculator.calculateFinalPrice(items, campaigns);

        assertThat(result).isEqualByComparingTo("540");
    }

    @Test
    void categoryPercentageOnTop_matchesPdfExample() {
        List<CartItem> items = List.of(
                new CartItem("T-Shirt", ItemCategory.CLOTHING, new BigDecimal("350")),
                new CartItem("Hoodie", ItemCategory.CLOTHING, new BigDecimal("700")),
                new CartItem("Watch", ItemCategory.ELECTRONICS, new BigDecimal("850")),
                new CartItem("Bag", ItemCategory.ACCESSORIES, new BigDecimal("640"))
        );
        List<DiscountCampaign> campaigns = List.of(
                new CategoryPercentageOnTopCampaign(ItemCategory.CLOTHING, new BigDecimal("15")));

        BigDecimal result = calculator.calculateFinalPrice(items, campaigns);

        assertThat(result).isEqualByComparingTo("2382.5");
    }

    @Test
    void pointsOnTop_matchesPdfExample() {
        List<CartItem> items = List.of(
                new CartItem("T-Shirt", ItemCategory.CLOTHING, new BigDecimal("350")),
                new CartItem("Hat", ItemCategory.ACCESSORIES, new BigDecimal("250")),
                new CartItem("Belt", ItemCategory.ACCESSORIES, new BigDecimal("230"))
        );
        List<DiscountCampaign> campaigns = List.of(new PointsOnTopCampaign(new BigDecimal("68")));

        BigDecimal result = calculator.calculateFinalPrice(items, campaigns);

        assertThat(result).isEqualByComparingTo("762");
    }

    @Test
    void seasonal_matchesPdfExample() {
        List<CartItem> items = List.of(
                new CartItem("T-Shirt", ItemCategory.CLOTHING, new BigDecimal("350")),
                new CartItem("Hat", ItemCategory.ACCESSORIES, new BigDecimal("250")),
                new CartItem("Belt", ItemCategory.ACCESSORIES, new BigDecimal("230"))
        );
        List<DiscountCampaign> campaigns = List.of(
                new SeasonalCampaign(new BigDecimal("300"), new BigDecimal("40")));

        BigDecimal result = calculator.calculateFinalPrice(items, campaigns);

        assertThat(result).isEqualByComparingTo("750");
    }

    // ---- Combining multiple campaigns & ordering ----

    @Test
    void appliesCampaignsInCouponThenOnTopThenSeasonalOrder_regardlessOfInputOrder() {
        List<CartItem> items = List.of(
                new CartItem("T-Shirt", ItemCategory.CLOTHING, new BigDecimal("350")),
                new CartItem("Hat", ItemCategory.ACCESSORIES, new BigDecimal("250")),
                new CartItem("Belt", ItemCategory.ACCESSORIES, new BigDecimal("230"))
        );
        // Supplied out of order on purpose: Seasonal, On Top, Coupon
        List<DiscountCampaign> campaigns = List.of(
                new SeasonalCampaign(new BigDecimal("300"), new BigDecimal("40")),
                new PointsOnTopCampaign(new BigDecimal("68")),
                new FixedAmountCouponCampaign(new BigDecimal("50"))
        );

        // total = 830
        // coupon: 830 - 50 = 780
        // on-top points: cap = 20% of 780 = 156, points 68 < cap -> 780 - 68 = 712
        // seasonal: floor(712/300) = 2 -> 712 - 80 = 632
        BigDecimal result = calculator.calculateFinalPrice(items, campaigns);

        assertThat(result).isEqualByComparingTo("632");
    }

    @Test
    void noCampaigns_returnsCartTotal() {
        List<CartItem> items = List.of(
                new CartItem("T-Shirt", ItemCategory.CLOTHING, new BigDecimal("350"))
        );

        assertThat(calculator.calculateFinalPrice(items, List.of())).isEqualByComparingTo("350");
        assertThat(calculator.calculateFinalPrice(items, null)).isEqualByComparingTo("350");
    }

    @Test
    void discountNeverMakesTotalNegative() {
        List<CartItem> items = List.of(
                new CartItem("Cheap Item", ItemCategory.ACCESSORIES, new BigDecimal("10"))
        );
        List<DiscountCampaign> campaigns = List.of(new FixedAmountCouponCampaign(new BigDecimal("1000")));

        BigDecimal result = calculator.calculateFinalPrice(items, campaigns);

        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    void quantityGreaterThanOne_isIncludedInTotal() {
        List<CartItem> items = List.of(
                new CartItem("T-Shirt", ItemCategory.CLOTHING, new BigDecimal("350"), 2)
        );

        BigDecimal result = calculator.calculateFinalPrice(items, List.of());

        assertThat(result).isEqualByComparingTo("700");
    }

    // ---- Validation / resilience to bad input ----

    @Test
    void nullCartItems_throws() {
        assertThatThrownBy(() -> calculator.calculateFinalPrice(null, List.of()))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void emptyCartItems_throws() {
        assertThatThrownBy(() -> calculator.calculateFinalPrice(List.of(), List.of()))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void twoCouponCampaigns_throws() {
        List<CartItem> items = List.of(new CartItem("T-Shirt", ItemCategory.CLOTHING, new BigDecimal("350")));
        List<DiscountCampaign> campaigns = List.of(
                new FixedAmountCouponCampaign(new BigDecimal("50")),
                new PercentageCouponCampaign(new BigDecimal("10"))
        );

        assertThatThrownBy(() -> calculator.calculateFinalPrice(items, campaigns))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void twoOnTopCampaigns_throws() {
        List<CartItem> items = List.of(new CartItem("T-Shirt", ItemCategory.CLOTHING, new BigDecimal("350")));
        List<DiscountCampaign> campaigns = List.of(
                new CategoryPercentageOnTopCampaign(ItemCategory.CLOTHING, new BigDecimal("15")),
                new PointsOnTopCampaign(new BigDecimal("10"))
        );

        assertThatThrownBy(() -> calculator.calculateFinalPrice(items, campaigns))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void nullCampaignInList_throws() {
        List<CartItem> items = List.of(new CartItem("T-Shirt", ItemCategory.CLOTHING, new BigDecimal("350")));
        List<DiscountCampaign> campaigns = new java.util.ArrayList<>();
        campaigns.add(null);

        assertThatThrownBy(() -> calculator.calculateFinalPrice(items, campaigns))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void negativeFixedAmount_throwsOnConstruction() {
        assertThatThrownBy(() -> new FixedAmountCouponCampaign(new BigDecimal("-1")))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void percentageOver100_throwsOnConstruction() {
        assertThatThrownBy(() -> new PercentageCouponCampaign(new BigDecimal("150")))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void negativePercentage_throwsOnConstruction() {
        assertThatThrownBy(() -> new PercentageCouponCampaign(new BigDecimal("-10")))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void negativePoints_throwsOnConstruction() {
        assertThatThrownBy(() -> new PointsOnTopCampaign(new BigDecimal("-5")))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void nullCategory_throwsOnConstruction() {
        assertThatThrownBy(() -> new CategoryPercentageOnTopCampaign(null, new BigDecimal("10")))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void seasonalWithZeroOrNegativeEvery_throwsOnConstruction() {
        assertThatThrownBy(() -> new SeasonalCampaign(BigDecimal.ZERO, new BigDecimal("40")))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
        assertThatThrownBy(() -> new SeasonalCampaign(new BigDecimal("-100"), new BigDecimal("40")))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void seasonalWithNegativeDiscount_throwsOnConstruction() {
        assertThatThrownBy(() -> new SeasonalCampaign(new BigDecimal("300"), new BigDecimal("-40")))
                .isInstanceOf(InvalidDiscountConfigurationException.class);
    }

    @Test
    void cartItemWithNegativePrice_throwsOnConstruction() {
        assertThatThrownBy(() -> new CartItem("Bad", ItemCategory.CLOTHING, new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cartItemWithZeroQuantity_throwsOnConstruction() {
        assertThatThrownBy(() -> new CartItem("Bad", ItemCategory.CLOTHING, new BigDecimal("10"), 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cartItemWithBlankName_throwsOnConstruction() {
        assertThatThrownBy(() -> new CartItem(" ", ItemCategory.CLOTHING, new BigDecimal("10")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void pointsCappedAt20PercentOfTotal() {
        List<CartItem> items = List.of(new CartItem("Item", ItemCategory.ELECTRONICS, new BigDecimal("1000")));
        // 500 points requested, cap is 20% of 1000 = 200
        List<DiscountCampaign> campaigns = List.of(new PointsOnTopCampaign(new BigDecimal("500")));

        BigDecimal result = calculator.calculateFinalPrice(items, campaigns);

        assertThat(result).isEqualByComparingTo("800");
    }
}
