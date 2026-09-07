package com.saarisht.eventhub.paymentservice.controller;

import com.saarisht.eventhub.paymentservice.dto.ChargeRequest;
import com.saarisht.eventhub.paymentservice.dto.ChargeResponse;
import com.saarisht.eventhub.paymentservice.entity.Payment;
import com.saarisht.eventhub.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments/charge")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChargeResponse charge(@RequestBody ChargeRequest request) {
        Payment payment = paymentService.initiateCharge(request.amount());
        return ChargeResponse.from(payment);
    }
}
