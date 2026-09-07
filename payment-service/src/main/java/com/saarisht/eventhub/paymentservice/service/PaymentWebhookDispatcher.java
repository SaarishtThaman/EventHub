package com.saarisht.eventhub.paymentservice.service;

import com.saarisht.eventhub.paymentservice.dto.PaymentWebhookPayload;
import com.saarisht.eventhub.paymentservice.entity.Payment;
import com.saarisht.eventhub.paymentservice.entity.PaymentStatus;
import com.saarisht.eventhub.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Random;

/**
 * Simulates a real payment gateway's async settlement: charge requests return
 * immediately as PENDING, and the real outcome shows up later via webhook.
 * This class owns that "later" — it must be a separate bean from
 * PaymentService, because @Async only works through Spring's AOP proxy, and
 * a method calling another @Async method on the same bean (self-invocation)
 * skips the proxy and just runs synchronously.
 */
@Service
public class PaymentWebhookDispatcher {

    private static final int MIN_DELAY_MS = 2000;
    private static final int MAX_DELAY_MS = 5000;
    private static final int SUCCESS_RATE_PERCENT = 90;

    private final PaymentRepository paymentRepository;
    private final RestClient restClient;
    private final String webhookUrl;
    private final Random random = new Random();

    public PaymentWebhookDispatcher(PaymentRepository paymentRepository,
                                     RestClient.Builder restClientBuilder,
                                     @Value("${eventhub.booking-service.webhook-url}") String webhookUrl) {
        this.paymentRepository = paymentRepository;
        this.restClient = restClientBuilder.build();
        this.webhookUrl = webhookUrl;
    }

    @Async
    public void settleAndNotify(Long paymentId) {
        sleep(MIN_DELAY_MS + random.nextInt(MAX_DELAY_MS - MIN_DELAY_MS));

        PaymentStatus outcome = random.nextInt(100) < SUCCESS_RATE_PERCENT
                ? PaymentStatus.SUCCEEDED
                : PaymentStatus.FAILED;

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("Payment vanished before settlement: " + paymentId));
        payment.setStatus(outcome);
        paymentRepository.save(payment);

        restClient.post()
                .uri(webhookUrl)
                .body(new PaymentWebhookPayload(paymentId, outcome))
                .retrieve()
                .toBodilessEntity();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
