package com.example.shopingdiscount.common;

import java.math.BigDecimal;

/**
 * Shared constants used across the {@code model} and {@code service} packages.
 * Mirrors the {@code CommonConstant} pattern from wmos-consolidate-port.
 */
public class CommonConstant {

    // ─── Money formatting ─────────────────────────────────────────────────────
    public static final int MONEY_SCALE = 2;

    // ─── Percentage math ──────────────────────────────────────────────────────
    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    // ─── Points On Top campaign ───────────────────────────────────────────────
    // Discount from redeemed points is capped at this percentage of the total price.
    public static final BigDecimal POINTS_ON_TOP_CAP_PERCENTAGE = BigDecimal.valueOf(20);
}
