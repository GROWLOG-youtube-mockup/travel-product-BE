package com.travelservice.domain.cart.dto;

import java.time.LocalDate;

import com.travelservice.domain.cart.entity.Cart;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddToCartRequest {

	private Long productId;
	private Integer quantity;
	private LocalDate startDate;

	public Cart toEntity(User user, Product product) {
		return Cart.builder()
			.user(user)
			.product(product)
			.quantity(quantity)
			.startDate(startDate)
			.build();
	}

}
