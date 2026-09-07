package com.saarisht.eventhub.paymentservice.dto;

import com.saarisht.eventhub.paymentservice.entity.PaymentStatus;

/**
 * What payment-service POSTs to booking-service's webhook endpoint once a
 * charge resolves. booking-service correlates this back to a PaymentContext
 * it stored in Redis when it initiated the charge.
 */
public record PaymentWebhookPayload(Long paymentId, PaymentStatus status) {
}
