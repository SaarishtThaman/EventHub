package com.saarisht.eventhub.bookingservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class PaymentServiceClient {

    private final RestClient restClient;

    public PaymentServiceClient(RestClient.Builder restClientBuilder,
                                 @Value("${eventhub.payment-service.base-url}") String paymentServiceBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(paymentServiceBaseUrl).build();
    }

    public Long charge(BigDecimal amount) {
        ChargeResponse response = restClient.post()
                .uri("/payments/charge")
                .body(new ChargeRequest(amount))
                .retrieve()
                .body(ChargeResponse.class);
        return response.paymentId();
    }

    private record ChargeRequest(BigDecimal amount) {
    }

    private record ChargeResponse(Long paymentId, String status) {
    }
}
