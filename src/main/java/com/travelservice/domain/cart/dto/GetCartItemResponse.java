package com.travelservice.domain.cart.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetCartItemResponse {

	private Long cartItemId;
	private Long productId;
	private String productName;
	private Integer quantity;
	private Integer stockQuantity;
	private LocalDate startDate;
	private Integer price;
	private Integer totalPrice;
}
