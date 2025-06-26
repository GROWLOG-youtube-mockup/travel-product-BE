package com.travelservice.domain.order.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemInfo {

	@Schema(description = "상품 ID", example = "3")
	private Long productId;

	@Schema(description = "상품 이름", example = "부산 2박 3일 패키지")
	private String productName;

	@Schema(description = "여행 시작일", example = "2025-08-10")
	private LocalDate startDate;

	@Schema(description = "예약 인원 수", example = "2")
	private int peopleCount;

	@Schema(description = "상품 1인당 가격", example = "150000")
	private int price;

	@Schema(description = "총 결제 금액 (인원수 × 가격)", example = "300000")
	private int totalPrice;
}
