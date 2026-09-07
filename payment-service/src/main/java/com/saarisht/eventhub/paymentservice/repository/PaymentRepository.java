package com.saarisht.eventhub.paymentservice.repository;

import com.saarisht.eventhub.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
