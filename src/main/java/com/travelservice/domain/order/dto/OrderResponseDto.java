package com.travelservice.domain.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.travelservice.domain.order.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
	@JsonProperty("order_id")
	private Long orderId;
	@JsonProperty("order_date")
	private LocalDateTime orderDate;

	/*@JsonProperty("total_quantity")
	private int totalQuantity;*/

	@JsonProperty("total_price")
	private int totalPrice;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<OrderItemInfo> items; // 필요할 때만 세팅

	public OrderResponseDto(Order order) {
		log.info("Order: {}", order);
		log.info("OrderItems: {}", order.getOrderItems());

		this.orderId = order.getOrderId();
		this.orderDate = order.getOrderDate();

		this.totalPrice = order.getOrderItems().stream()
				.mapToInt(i -> i.getProduct().getPrice() * i.getPeopleCount())
				.sum();
	}

	public static OrderResponseDto withItems(Order order) {
		OrderResponseDto dto = new OrderResponseDto(order);
		dto.setItems(order.getOrderItems().stream()
			.map(i -> new OrderItemInfo(
				i.getProduct().getName(),
				i.getStartDate(),
				i.getPeopleCount(),
				i.getPrice(),
				i.getPrice() * i.getPeopleCount()))
			.toList());
		return dto;
	}
}
