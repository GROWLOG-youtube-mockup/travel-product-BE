package com.travelservice.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentApproveRequestDto {
	private String paymentKey;
	private String orderId;
	private int amount;
}
