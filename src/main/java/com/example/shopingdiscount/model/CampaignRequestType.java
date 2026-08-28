package com.example.shopingdiscount.model;

/**
 * The kind of campaign a {@link CampaignRequest} represents. Names map 1:1 to the
 * campaigns described in the assignment.
 */
public enum CampaignRequestType {
    /** Requires: amount */
    FIXED_AMOUNT_COUPON,
    /** Requires: percentage */
    PERCENTAGE_COUPON,
    /** Requires: category, percentage */
    CATEGORY_PERCENTAGE_ON_TOP,
    /** Requires: points */
    POINTS_ON_TOP,
    /** Requires: every, discount */
    SEASONAL
}
