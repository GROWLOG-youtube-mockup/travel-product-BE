package com.travelservice.domain.order.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 요청 DTO")
public class OrderRequestDto {
	@Schema(
		description = "주문 항목 요청 DTO - 클라이언트가 주문 시 선택한 여행 상품 정보 (상품 ID, 인원 수, 시작일 포함)",
		requiredMode = Schema.RequiredMode.REQUIRED,
		example = """
			[
				{
					"peopleCount": 2,
					"product_id": 3,
					"start_date": "2025-08-01"
				}
			]
			"""
	)
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
