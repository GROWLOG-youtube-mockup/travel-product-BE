package com.travelservice.domain.order.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.travelservice.domain.order.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
	@JsonProperty("order_id")
	private Long orderId;
	@JsonProperty("order_date")
	private LocalDateTime orderDate;
	@JsonProperty("total_quantity")
	private int totalQuantity;
	@JsonProperty("total_price")
	private int totalPrice;

	public OrderResponseDto(Order order) {
		this.orderId = order.getOrderId();
		this.orderDate = order.getOrderDate();
		this.totalQuantity = order.getTotalQuantity();
		this.totalPrice = order.getOrderItems().stream()
				.mapToInt(i -> i.getProduct().getPrice() * i.getPeopleCount())
				.sum();
	}
}
