package com.travelservice.domain.order.dto;

import java.time.LocalDateTime;

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
	private Long orderId;
	private LocalDateTime orderDate;
	private int totalQuantity;
	private int totalPrice;

	public OrderResponseDto(Order order) {
		this.orderId = order.getOrderId();
		this.orderDate = order.getOrderDate();
		this.totalQuantity = order.getTotalQuantity();
		this.totalPrice = order.getItems().stream()
				.mapToInt(i -> i.getProduct().getPrice() * i.getPeopleCount())
				.sum();
	}
}
