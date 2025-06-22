package com.travelservice.domain.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInfoDto {
	private String paymentKey;
	private int amount;
}
