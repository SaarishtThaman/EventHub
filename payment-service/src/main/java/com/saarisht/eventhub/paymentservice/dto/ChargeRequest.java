package com.saarisht.eventhub.paymentservice.dto;

import java.math.BigDecimal;

public record ChargeRequest(BigDecimal amount) {
}
