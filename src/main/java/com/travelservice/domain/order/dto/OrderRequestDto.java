package com.travelservice.domain.order.dto;

import java.util.List;

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
	private PaymentInfo payment;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentInfo {
		private String cardNumber;
	}
}
