package com.saarisht.eventhub.bookingservice.controller;

import com.saarisht.eventhub.bookingservice.dto.BookingRequest;
import com.saarisht.eventhub.bookingservice.dto.BookingResponse;
import com.saarisht.eventhub.bookingservice.dto.BookingSummaryResponse;
import com.saarisht.eventhub.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> initiateBooking(Authentication authentication,
                                                             @Valid @RequestBody BookingRequest request) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.accepted().body(bookingService.initiateBooking(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<BookingSummaryResponse>> getMyBookings(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(bookingService.getBookingsForUser(userId));
    }
}
