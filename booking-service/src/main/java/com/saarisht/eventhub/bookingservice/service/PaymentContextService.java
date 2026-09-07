package com.saarisht.eventhub.bookingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saarisht.eventhub.bookingservice.dto.PaymentContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PaymentContextService {

    private static final String KEY_PREFIX = "payment-context:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration contextTtl;

    public PaymentContextService(StringRedisTemplate redisTemplate,
                                  ObjectMapper objectMapper,
                                  @Value("${booking.payment-context.ttl-seconds:900}") long ttlSeconds) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.contextTtl = Duration.ofSeconds(ttlSeconds);
    }

    public void storeContext(Long paymentId, PaymentContext context) {
        try {
            String json = objectMapper.writeValueAsString(context);
            redisTemplate.opsForValue().set(key(paymentId), json, contextTtl);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize payment context for paymentId " + paymentId, e);
        }
    }

    public PaymentContext getContext(Long paymentId) {
        String json = redisTemplate.opsForValue().get(key(paymentId));
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, PaymentContext.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize payment context for paymentId " + paymentId, e);
        }
    }

    public void deleteContext(Long paymentId) {
        redisTemplate.delete(key(paymentId));
    }

    private String key(Long paymentId) {
        return KEY_PREFIX + paymentId;
    }
}
