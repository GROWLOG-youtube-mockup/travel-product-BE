package com.travelservice.domain.admin.dto.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOrderDetailResponseDto {

	@JsonProperty("order_id")
	private Long orderId;

	private UserDto user;

	private String status;

	@JsonProperty("total_quantity")
	private Integer totalQuantity;

	@JsonProperty("order_date")
	private LocalDateTime orderDate;

	@JsonProperty("cancel_date")
	private LocalDateTime cancelDate;

	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	@JsonProperty("updated_at")
	private LocalDateTime updatedAt;

	@JsonProperty("order_items")
	private List<OrderItemDto> orderItems;

	private PaymentDto payment;

	// 내부 static 클래스들
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UserDto {
		@JsonProperty("user_id")
		private Long userId;
		private String name;
		private String email;
		@JsonProperty("phone_number")
		private String phoneNumber;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class OrderItemDto {
		@JsonProperty("order_item_id")
		private Long orderItemId;

		private ProductDto product;

		@JsonProperty("people_count")
		private Integer peopleCount;

		@JsonProperty("start_date")
		private LocalDate startDate;

		@JsonProperty("created_at")
		private LocalDateTime createdAt;

		@JsonProperty("updated_at")
		private LocalDateTime updatedAt;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProductDto {
		@JsonProperty("product_id")
		private Long productId;
		private String name;
		private Integer price;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class PaymentDto {
		@JsonProperty("payment_id")
		private Long paymentId;
		@JsonProperty("card_number")
		private String cardNumber;
		private String status;
		@JsonProperty("payment_datetime")
		private LocalDateTime paymentDatetime;
	}
}
