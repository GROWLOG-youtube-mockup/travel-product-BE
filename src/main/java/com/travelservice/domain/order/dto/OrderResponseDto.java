package com.travelservice.domain.order.dto;

import com.travelservice.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
	private Long orderId;
	private LocalDateTime orderDate;
	private int totalQuantity;

	public OrderResponseDto(Order order) {
		this.orderId = order.getOrderId();
		this.orderDate = order.getOrderDate();
		this.totalQuantity = order.getTotalQuantity();
	}
}
