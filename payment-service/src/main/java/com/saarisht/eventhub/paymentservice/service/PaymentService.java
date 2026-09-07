package com.saarisht.eventhub.paymentservice.service;

import com.saarisht.eventhub.paymentservice.entity.Payment;
import com.saarisht.eventhub.paymentservice.entity.PaymentStatus;
import com.saarisht.eventhub.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentWebhookDispatcher webhookDispatcher;

    public PaymentService(PaymentRepository paymentRepository, PaymentWebhookDispatcher webhookDispatcher) {
        this.paymentRepository = paymentRepository;
        this.webhookDispatcher = webhookDispatcher;
    }

    @Transactional
    public Payment initiateCharge(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Charge amount must be positive");
        }

        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(Instant.now());
        payment = paymentRepository.save(payment);

        webhookDispatcher.settleAndNotify(payment.getId());

        return payment;
    }
}
