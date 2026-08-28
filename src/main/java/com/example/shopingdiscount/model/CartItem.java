package com.example.shopingdiscount.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * A single line item in the shopping cart.
 * Immutable value object; price is the unit price and quantity defaults to 1.
 */
@Data
public final class CartItem {

    private final String name;
    private final ItemCategory category;
    private final BigDecimal unitPrice;
    private final int quantity;

    public CartItem(String name, ItemCategory category, BigDecimal unitPrice, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name must not be blank");
        }
        if (category == null) {
            throw new IllegalArgumentException("Item category must not be null");
        }
        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Item unit price must not be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Item quantity must be positive");
        }
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public CartItem(String name, ItemCategory category, BigDecimal unitPrice) {
        this(name, category, unitPrice, 1);
    }

    /** Total price of this line item: unitPrice * quantity. */
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
