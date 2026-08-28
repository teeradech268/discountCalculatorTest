package com.example.shopingdiscount.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request payload for a single cart item.
 * Example JSON:
 * {"name": "T-Shirt", "category": "CLOTHING", "unitPrice": 350, "quantity": 1}
 */
public record CartItemRequest(

        @NotBlank(message = "name must not be blank")
        String name,

        @NotNull(message = "category must be one of CLOTHING, ACCESSORIES, ELECTRONICS")
        ItemCategory category,

        @NotNull(message = "unitPrice is required")
        @DecimalMin(value = "0", inclusive = true, message = "unitPrice must not be negative")
        BigDecimal unitPrice,

        @Min(value = 1, message = "quantity must be at least 1")
        Integer quantity
) {
    /** Defaults quantity to 1 when not supplied. */
    public int quantityOrDefault() {
        return quantity == null ? 1 : quantity;
    }
}
