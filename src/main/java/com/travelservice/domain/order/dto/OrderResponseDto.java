package com.travelservice.domain.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.travelservice.domain.order.entity.Order;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "주문 응답 DTO - 주문 완료 후 클라이언트에 반환되는 주문 요약 정보와 항목 리스트")
public class OrderResponseDto {

	@Schema(description = "주문 ID", example = "1001")
	@JsonProperty("order_id")
	private Long orderId;

	@Schema(description = "주문 일시 (ISO 8601 형식)", example = "2025-08-01T15:30:00")
	@JsonProperty("order_date")
	private LocalDateTime orderDate;

	/*@JsonProperty("total_quantity")
	private int totalQuantity;*/

	@Schema(description = "총 결제 금액 (모든 항목의 총합)", example = "300000")
	@JsonProperty("total_price")
	private int totalPrice;

	@Schema(description = "주문 항목 리스트 (상품 정보, 인원수, 가격 등 포함)")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<OrderItemInfo> items; // 필요할 때만 세팅

	// 생성자: 주문 요약만 포함
	public OrderResponseDto(Order order) {
		log.info("Order: {}", order);
		log.info("OrderItems: {}", order.getOrderItems());

		this.orderId = order.getOrderId();
		this.orderDate = order.getOrderDate();

		this.totalPrice = order.getOrderItems().stream()
			.mapToInt(i -> i.getProduct().getPrice() * i.getPeopleCount())
			.sum();
	}

	// 주문 + 항목 정보 포함 응답
	public static OrderResponseDto withItems(Order order) {
		OrderResponseDto dto = new OrderResponseDto(order);
		dto.setItems(order.getOrderItems().stream()
			.map(i -> new OrderItemInfo(
				i.getProduct().getProductId(),
				i.getProduct().getName(),
				i.getStartDate(),
				i.getPeopleCount(),
				i.getProduct().getPrice(),
				i.getProduct().getPrice() * i.getPeopleCount()))
			.toList());
		return dto;
	}
}
