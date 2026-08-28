package com.saarisht.eventhub.bookingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * booking-service only ever verifies tokens, never issues them, so unlike
 * auth-service this loads the public key only — there is no private key
 * anywhere in this service.
 */
@Configuration
public class JwtKeyConfig {

    @Bean
    public PublicKey jwtPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = readPemBytes("keys/public_key.pem");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private byte[] readPemBytes(String classpathLocation) throws IOException {
        String pem;
        try (InputStream is = new ClassPathResource(classpathLocation).getInputStream()) {
            pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        String base64Body = pem
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");

        return Base64.getDecoder().decode(base64Body);
    }
}
