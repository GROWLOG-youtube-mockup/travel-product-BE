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
public class OrderItemInfo {
	private Long productId;
	private String productName;
	private LocalDate startDate;
	private int peopleCount;
	private int price;
	private int totalPrice;
}
