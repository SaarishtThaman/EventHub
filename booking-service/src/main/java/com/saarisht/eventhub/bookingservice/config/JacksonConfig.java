package com.saarisht.eventhub.bookingservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 4 auto-configures a Jackson 3 (tools.jackson.*) ObjectMapper by
 * default, but PaymentContextService needs the classic com.fasterxml.jackson
 * ObjectMapper — the same one jjwt-jackson still depends on, and already on
 * the classpath. Spring doesn't register a bean for that type on its own
 * anymore, so it's defined explicitly here.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
