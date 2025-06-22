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
public class PaymentApproveRequestDto {
	@JsonProperty("payment_key")
	private String paymentKey;
	@JsonProperty("order_id")
	private Long orderId;
	private int amount;
	@JsonProperty("payment_gateway")
	private String paymentGateway;
	@JsonProperty("transaction_id")
	private String transactionId;
}
