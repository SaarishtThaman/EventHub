package com.saarisht.eventhub.bookingservice.dto;

public record PaymentWebhookPayload(Long paymentId, PaymentOutcome status) {
}
