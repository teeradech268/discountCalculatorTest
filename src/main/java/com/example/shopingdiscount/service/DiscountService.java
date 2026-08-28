package com.example.shopingdiscount.service;

import com.example.shopingdiscount.common.CommonConstant;
import com.example.shopingdiscount.config.handler.InvalidDiscountConfigurationException;
import com.example.shopingdiscount.model.CampaignRequest;
import com.example.shopingdiscount.model.CartItem;
import com.example.shopingdiscount.model.CartItemRequest;
import com.example.shopingdiscount.model.DiscountRequest;
import com.example.shopingdiscount.model.DiscountResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Calculates the final price of an order by applying a set of discount campaigns to
 * the items in a shopping cart, and maps API request payloads into the domain objects
 * needed to do so.
 *
 * <p>Rules enforced:
 * <ul>
 *     <li>Only one campaign per {@link CampaignType} may be applied to an order.</li>
 *     <li>Campaigns are applied in the fixed order: COUPON -&gt; ON_TOP -&gt; SEASONAL,
 *         regardless of the order they are supplied in.</li>
 *     <li>The running total is never allowed to go below zero.</li>
 * </ul>
 */
@Service
public class DiscountService {

    /**
     * Maps the request payload to domain objects and calculates the final price.
     * Used by {@link com.example.shopingdiscount.controller.DiscountController}.
     */
    public DiscountResponse calculate(DiscountRequest request) {
        List<CartItem> items = toCartItems(request.items());
        List<DiscountCampaign> campaigns = toCampaigns(request.campaigns());

        BigDecimal finalPrice = calculateFinalPrice(items, campaigns);

        return new DiscountResponse(request.items(), request.campaigns(), finalPrice);
    }

    /**
     * Calculates the final price after applying the given campaigns to the cart items.
     *
     * @param items      the items in the shopping cart; must not be null or empty
     * @param campaigns  the discount campaigns to apply; may be null or empty (no discount applied)
     * @return the final price, rounded to 2 decimal places, never negative
     * @throws InvalidDiscountConfigurationException if the cart is empty/null or more than
     *         one campaign of the same {@link CampaignType} is supplied
     */
    public BigDecimal calculateFinalPrice(List<CartItem> items, List<DiscountCampaign> campaigns) {
        if (items == null || items.isEmpty()) {
            throw new InvalidDiscountConfigurationException("Cart items must not be null or empty");
        }

        BigDecimal total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DiscountCampaign> orderedCampaigns = validateAndOrder(campaigns);

        for (DiscountCampaign campaign : orderedCampaigns) {
            BigDecimal discount = campaign.calculateDiscount(total, items);
            if (discount == null || discount.signum() < 0) {
                throw new InvalidDiscountConfigurationException(
                        "Campaign produced an invalid negative discount: " + campaign.getDescription());
            }
            total = total.subtract(discount);
            if (total.signum() < 0) {
                total = BigDecimal.ZERO;
            }
        }

        return total.setScale(CommonConstant.MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private List<DiscountCampaign> validateAndOrder(List<DiscountCampaign> campaigns) {
        if (campaigns == null || campaigns.isEmpty()) {
            return List.of();
        }

        Map<CampaignType, DiscountCampaign> byType = new EnumMap<>(CampaignType.class);
        for (DiscountCampaign campaign : campaigns) {
            if (campaign == null) {
                throw new InvalidDiscountConfigurationException("Campaign list must not contain null entries");
            }
            DiscountCampaign existing = byType.put(campaign.getType(), campaign);
            if (existing != null) {
                throw new InvalidDiscountConfigurationException(
                        "Only one campaign per category is allowed; duplicate category: " + campaign.getType());
            }
        }

        return byType.values().stream()
                .sorted(Comparator.comparing(DiscountCampaign::getType))
                .toList();
    }

    // ---- Request DTO -> domain object mapping ----

    public CartItem toCartItem(CartItemRequest request) {
        return new CartItem(request.name(), request.category(), request.unitPrice(), request.quantityOrDefault());
    }

    public List<CartItem> toCartItems(List<CartItemRequest> requests) {
        return requests.stream().map(this::toCartItem).toList();
    }

    public DiscountCampaign toCampaign(CampaignRequest request) {
        if (request.type() == null) {
            throw new InvalidDiscountConfigurationException("Campaign type is required");
        }
        return switch (request.type()) {
            case FIXED_AMOUNT_COUPON -> new FixedAmountCouponCampaign(
                    required(request.amount(), "amount", request.type()));
            case PERCENTAGE_COUPON -> new PercentageCouponCampaign(
                    required(request.percentage(), "percentage", request.type()));
            case CATEGORY_PERCENTAGE_ON_TOP -> new CategoryPercentageOnTopCampaign(
                    requiredCategory(request), required(request.percentage(), "percentage", request.type()));
            case POINTS_ON_TOP -> new PointsOnTopCampaign(
                    required(request.points(), "points", request.type()));
            case SEASONAL -> new SeasonalCampaign(
                    required(request.every(), "every", request.type()),
                    required(request.discount(), "discount", request.type()));
        };
    }

    public List<DiscountCampaign> toCampaigns(List<CampaignRequest> requests) {
        if (requests == null) {
            return List.of();
        }
        return requests.stream().map(this::toCampaign).toList();
    }

    private static <T> T required(T value, String fieldName, Object type) {
        if (value == null) {
            throw new InvalidDiscountConfigurationException(
                    "Field '" + fieldName + "' is required for campaign type " + type);
        }
        return value;
    }

    private static com.example.shopingdiscount.model.ItemCategory requiredCategory(CampaignRequest request) {
        return Objects.requireNonNullElseGet(request.category(), () -> {
            throw new InvalidDiscountConfigurationException(
                    "Field 'category' is required for campaign type " + request.type());
        });
    }
}
