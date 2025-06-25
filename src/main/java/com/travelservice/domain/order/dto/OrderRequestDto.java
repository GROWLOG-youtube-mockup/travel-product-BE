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
}
