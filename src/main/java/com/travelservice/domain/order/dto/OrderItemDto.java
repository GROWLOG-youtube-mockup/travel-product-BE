package com.travelservice.domain.order.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
	private ProductInfo product;
	private int peopleCount;
	private LocalDate startDate;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ProductInfo {
		private Long id;
	}
}
