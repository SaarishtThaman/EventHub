package com.saarisht.eventhub.bookingservice.service;

import com.saarisht.eventhub.bookingservice.client.EventServiceClient;
import com.saarisht.eventhub.bookingservice.dto.PaymentContext;
import com.saarisht.eventhub.bookingservice.dto.PaymentOutcome;
import com.saarisht.eventhub.bookingservice.entity.Booking;
import com.saarisht.eventhub.bookingservice.entity.BookingSeat;
import com.saarisht.eventhub.bookingservice.entity.BookingStatus;
import com.saarisht.eventhub.bookingservice.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentWebhookService {

    private static final Logger log = LoggerFactory.getLogger(PaymentWebhookService.class);

    private final PaymentContextService paymentContextService;
    private final SeatLockService seatLockService;
    private final EventServiceClient eventServiceClient;
    private final BookingRepository bookingRepository;

    public PaymentWebhookService(PaymentContextService paymentContextService,
                                  SeatLockService seatLockService,
                                  EventServiceClient eventServiceClient,
                                  BookingRepository bookingRepository) {
        this.paymentContextService = paymentContextService;
        this.seatLockService = seatLockService;
        this.eventServiceClient = eventServiceClient;
        this.bookingRepository = bookingRepository;
    }

    public void handle(Long paymentId, PaymentOutcome status) {
        PaymentContext context = paymentContextService.getContext(paymentId);
        if (context == null) {
            // Either already handled (webhook redelivered), or the context TTL
            // lapsed before this webhook arrived. Nothing to correlate against,
            // so this is a no-op rather than an error.
            log.warn("No payment context found for paymentId {}, ignoring webhook", paymentId);
            return;
        }

        if (status == PaymentOutcome.FAILED) {
            handleFailed(paymentId, context);
        } else {
            handleSucceeded(paymentId, context);
        }
    }

    private void handleFailed(Long paymentId, PaymentContext context) {
        for (Long eventSeatId : context.eventSeatIds()) {
            seatLockService.releaseLock(eventSeatId, context.userId());
        }
        paymentContextService.deleteContext(paymentId);
    }

    private void handleSucceeded(Long paymentId, PaymentContext context) {
        boolean confirmed = eventServiceClient.confirmSeats(context.eventId(), context.eventSeatIds());

        if (!confirmed) {
            // The seat lock outlived its TTL and someone else took the seat
            // before this (slow) webhook arrived. Payment already succeeded
            // with no booking to show for it — this needs a refund path,
            // which does not exist yet. Logging loudly until it does.
            log.error("Payment {} succeeded but seat confirmation failed for event {} — refund needed, not yet implemented",
                    paymentId, context.eventId());
            for (Long eventSeatId : context.eventSeatIds()) {
                seatLockService.releaseLock(eventSeatId, context.userId());
            }
            paymentContextService.deleteContext(paymentId);
            return;
        }

        Booking booking = new Booking();
        booking.setUserId(context.userId());
        booking.setEventId(context.eventId());
        booking.setTotalAmount(context.amount());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(Instant.now());

        for (Long eventSeatId : context.eventSeatIds()) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setEventSeatId(eventSeatId);
            booking.getSeats().add(bookingSeat);
        }

        bookingRepository.save(booking);

        for (Long eventSeatId : context.eventSeatIds()) {
            seatLockService.releaseLock(eventSeatId, context.userId());
        }
        paymentContextService.deleteContext(paymentId);
    }
}
