package com.example.shopingdiscount.config.handler;

/**
 * Thrown when the set of discount campaigns supplied is invalid, e.g. more than
 * one campaign from the same {@link com.example.shopingdiscount.service.CampaignType},
 * or a campaign was configured with invalid parameters (negative amount, percentage out of range, etc).
 */
public class InvalidDiscountConfigurationException extends RuntimeException {

    public InvalidDiscountConfigurationException(String message) {
        super(message);
    }
}
