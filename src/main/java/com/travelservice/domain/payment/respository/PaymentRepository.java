package com.travelservice.domain.payment.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Optional<Payment> findByOrder_OrderId(Long orderId);

	Optional<Payment> findByOrder(Order order);
}
