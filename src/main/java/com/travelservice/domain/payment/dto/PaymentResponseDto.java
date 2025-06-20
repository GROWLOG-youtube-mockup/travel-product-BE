package com.travelservice.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {
	@JsonProperty("payment_id")
	private Long paymentId;
	private String status;
	private String method;
	@JsonProperty("paid_at")
	private String paidAt;
}
