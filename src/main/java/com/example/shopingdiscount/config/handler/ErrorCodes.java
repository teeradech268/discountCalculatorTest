package com.example.shopingdiscount.config.handler;

/**
 * Error codes returned to API clients, following the same RE/BE/SE convention used
 * across our services:
 * RE = Request error (client sent something invalid)
 * BE = Business error (request is well-formed but violates a business rule)
 * SE = System error (unexpected server-side failure)
 */
public enum ErrorCodes {

    // RE - request/validation errors
    RE00002("Body message field required/invalid"),
    RE00003("Not readable field data"),

    // BE - business errors
    BE00001("Invalid discount campaign configuration"),

    // SE - system errors
    SE00001("Internal System Error");

    private final String message;

    ErrorCodes(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
