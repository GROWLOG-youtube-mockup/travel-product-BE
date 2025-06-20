package com.travelservice.domain.order.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
	private List<OrderItemDto> items;
	@JsonProperty("payment_method")
	private String paymentMethod;
	private PaymentInfo payment;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentInfo {
		@JsonProperty("card_number")
		private String cardNumber;
	}
}
