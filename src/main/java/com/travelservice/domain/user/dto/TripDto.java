package com.travelservice.domain.user.dto;

import java.time.LocalDate;

import com.travelservice.domain.order.entity.OrderItem;
import com.travelservice.domain.product.entity.Product;

import io.swagger.v3.oas.annotations.media.Schema;

public record TripDto(
	@Schema(description = "주문 항목 ID", example = "101")
	Long orderItemId,

	@Schema(description = "주문 전체 ID", example = "11")
	Long orderId,

	@Schema(description = "상품 ID", example = "5")
	Long productId,

	@Schema(description = "여행 상품 제목", example = "제주도 2박 3일 자유 여행")
	String title,

	@Schema(description = "여행 시작일", example = "2025-07-01")
	LocalDate startDate,

	@Schema(description = "여행 종료일", example = "2025-07-03")
	LocalDate endDate,

	@Schema(description = "여행 기간(일수)", example = "3")
	int duration,

	@Schema(description = "예약 인원 수", example = "2")
	int peopleCount,

	@Schema(description = "상품 가격", example = "299000")
	int price,

	@Schema(description = "대표 이미지 URL", example = "https://cdn.example.com/images/jeju.jpg")
	String thumbnailUrl
) {
	public static TripDto from(OrderItem orderItem) {
		Product product = orderItem.getProduct();
		LocalDate endDate = orderItem.getStartDate().plusDays(product.getDuration() - 1);

		String thumbnail = product.getImages().isEmpty()
			? null
			: product.getImages().get(0).getImageUrl();

		return new TripDto(
			orderItem.getOrderItemId(),
			orderItem.getOrder().getOrderId(),
			product.getProductId(),
			product.getName(),
			orderItem.getStartDate(),
			endDate,
			product.getDuration(),
			orderItem.getPeopleCount(),
			product.getPrice(),
			thumbnail
		);
	}
}
