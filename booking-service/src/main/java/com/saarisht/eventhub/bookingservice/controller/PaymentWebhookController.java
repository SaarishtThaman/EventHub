package com.saarisht.eventhub.bookingservice.controller;

import com.saarisht.eventhub.bookingservice.dto.PaymentWebhookPayload;
import com.saarisht.eventhub.bookingservice.service.PaymentWebhookService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentWebhookController {

    private final PaymentWebhookService paymentWebhookService;

    public PaymentWebhookController(PaymentWebhookService paymentWebhookService) {
        this.paymentWebhookService = paymentWebhookService;
    }

    @PostMapping("/webhooks/payment")
    public void receiveWebhook(@RequestBody PaymentWebhookPayload payload) {
        paymentWebhookService.handle(payload.paymentId(), payload.status());
    }
}
