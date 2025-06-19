package com.travelservice.domain.order.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
	@JsonProperty("product_id")
	private Long productId;
	private int quantity;
	@JsonProperty("start_date")
	private LocalDate startDate;
}
