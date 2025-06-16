package com.travelservice.domain.payment.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
