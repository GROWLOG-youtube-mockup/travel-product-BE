package com.travelservice.domain.order.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

	@NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다.")
	private List<OrderItemDto> items;
	//결제에 대한 내용은 실제 결제 서비스를 도입하기 때문에 결제는 PaymentApproveRequestDto를 통해 구현
/*
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
	}*/
}
