package com.travelservice.domain.payment.entity;

import java.time.LocalDateTime;

import com.travelservice.domain.order.entity.Order;
import com.travelservice.enums.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentId;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	private String paymentKey;
	private String method;
	private String cardNumber;
	private String accountNumber;
	private String bank;
	private String mobilePhone;

	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
	private LocalDateTime paidAt;
}
