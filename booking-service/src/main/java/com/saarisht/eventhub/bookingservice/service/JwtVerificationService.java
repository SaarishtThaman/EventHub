package com.saarisht.eventhub.bookingservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.PublicKey;

@Service
public class JwtVerificationService {

    private final PublicKey publicKey;

    public JwtVerificationService(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public Claims validateAndParse(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
