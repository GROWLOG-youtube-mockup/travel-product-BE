package com.travelservice.domain.order.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

	@Schema(description = "예약할 여행 상품의 ID", example = "3")
	@NotNull(message = "상품 ID는 필수입니다.")
	@JsonProperty("product_id")
	private Long productId;

	@Schema(description = "예약 인원 수", example = "2")
	@Min(value = 1, message = "예약 인원은 1 이상이어야 합니다.")
	private int peopleCount;

	@Schema(description = "여행 시작일 (yyyy-MM-dd)", example = "2025-08-01")
	@NotNull(message = "여행 시작일은 필수입니다.")
	@JsonProperty("start_date")
	private LocalDate startDate;
}
