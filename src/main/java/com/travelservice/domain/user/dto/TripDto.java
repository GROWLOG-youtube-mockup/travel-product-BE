package com.travelservice.domain.user.dto;

import java.time.LocalDate;

import com.travelservice.domain.order.entity.OrderItem;
import com.travelservice.domain.product.entity.Product;

public record TripDto(
	Long orderItemId,
	Long productId,
	String title,
	LocalDate startDate,
	LocalDate endDate,
	int duration,
	int peopleCount,
	int price,
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
