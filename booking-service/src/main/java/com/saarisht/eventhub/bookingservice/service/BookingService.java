package com.saarisht.eventhub.bookingservice.service;

import com.saarisht.eventhub.bookingservice.client.EventServiceClient;
import com.saarisht.eventhub.bookingservice.client.PaymentServiceClient;
import com.saarisht.eventhub.bookingservice.dto.BookingRequest;
import com.saarisht.eventhub.bookingservice.dto.BookingResponse;
import com.saarisht.eventhub.bookingservice.dto.BookingSummaryResponse;
import com.saarisht.eventhub.bookingservice.dto.EventSeatInfo;
import com.saarisht.eventhub.bookingservice.dto.PaymentContext;
import com.saarisht.eventhub.bookingservice.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final EventServiceClient eventServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final SeatLockService seatLockService;
    private final PaymentContextService paymentContextService;
    private final BookingRepository bookingRepository;

    public BookingService(EventServiceClient eventServiceClient,
                           PaymentServiceClient paymentServiceClient,
                           SeatLockService seatLockService,
                           PaymentContextService paymentContextService,
                           BookingRepository bookingRepository) {
        this.eventServiceClient = eventServiceClient;
        this.paymentServiceClient = paymentServiceClient;
        this.seatLockService = seatLockService;
        this.paymentContextService = paymentContextService;
        this.bookingRepository = bookingRepository;
    }

    public BookingResponse initiateBooking(Long userId, BookingRequest request) {
        Map<Long, EventSeatInfo> seatsById = eventServiceClient.getEventSeats(request.eventId()).stream()
                .collect(Collectors.toMap(EventSeatInfo::eventSeatId, Function.identity()));

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Long eventSeatId : request.eventSeatIds()) {
            EventSeatInfo seat = seatsById.get(eventSeatId);
            if (seat == null) {
                throw new IllegalArgumentException("Seat does not belong to this event: " + eventSeatId);
            }
            if (!"AVAILABLE".equals(seat.status())) {
                throw new IllegalStateException("Seat is not available: " + eventSeatId);
            }
            totalAmount = totalAmount.add(seat.price());
        }

        List<Long> acquiredLocks = new ArrayList<>();
        try {
            for (Long eventSeatId : request.eventSeatIds()) {
                if (!seatLockService.tryAcquireLock(eventSeatId, userId)) {
                    throw new IllegalStateException("Seat is currently being booked by someone else: " + eventSeatId);
                }
                acquiredLocks.add(eventSeatId);
            }
        } catch (RuntimeException e) {
            for (Long eventSeatId : acquiredLocks) {
                seatLockService.releaseLock(eventSeatId, userId);
            }
            throw e;
        }

        Long paymentId = paymentServiceClient.charge(totalAmount);
        paymentContextService.storeContext(paymentId,
                new PaymentContext(userId, request.eventId(), request.eventSeatIds(), totalAmount));

        return new BookingResponse(paymentId, "PAYMENT_PENDING");
    }

    public List<BookingSummaryResponse> getBookingsForUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(BookingSummaryResponse::from)
                .toList();
    }
}
