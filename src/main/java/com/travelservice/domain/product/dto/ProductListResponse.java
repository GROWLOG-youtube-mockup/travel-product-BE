package com.travelservice.domain.product.dto;

import com.travelservice.domain.product.entity.Product;

import lombok.Getter;

@Getter
public class ProductListResponse {

	private final String name;
	private final Integer price;

	public ProductListResponse(Product product) {
		this.name = product.getName();
		this.price = product.getPrice();
	}
}
