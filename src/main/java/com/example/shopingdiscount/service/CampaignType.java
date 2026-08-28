package com.example.shopingdiscount.service;

/**
 * The category a discount campaign belongs to. Only one campaign per category may be
 * applied to a single order, and campaigns are always applied in this declared order:
 * COUPON -&gt; ON_TOP -&gt; SEASONAL.
 */
public enum CampaignType {
    COUPON,
    ON_TOP,
    SEASONAL
}
