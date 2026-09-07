package com.saarisht.eventhub.paymentservice.dto;

import com.saarisht.eventhub.paymentservice.entity.Payment;
import com.saarisht.eventhub.paymentservice.entity.PaymentStatus;

public record ChargeResponse(Long paymentId, PaymentStatus status) {
    public static ChargeResponse from(Payment payment) {
        return new ChargeResponse(payment.getId(), payment.getStatus());
    }
}
