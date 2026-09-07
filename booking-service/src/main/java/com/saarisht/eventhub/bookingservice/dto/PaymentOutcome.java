package com.saarisht.eventhub.bookingservice.dto;

/**
 * Mirrors the terminal states of payment-service's PaymentStatus. Kept as a
 * separate type rather than a shared dependency — the two services should
 * not compile against each other's internal enums, only agree on the JSON
 * shape of the webhook contract.
 */
public enum PaymentOutcome {
    SUCCEEDED,
    FAILED
}
