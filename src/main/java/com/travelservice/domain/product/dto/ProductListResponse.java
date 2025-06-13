package com.travelservice.domain.product.dto;

import com.travelservice.domain.product.entity.Product;

import lombok.Getter;

@Getter
public class ProductListResponse {

	private final Integer productId;
	private final String name;
	private final Integer price;
	private final String thumbnailImage;

	public ProductListResponse(Product product) {
		this.productId = product.getProductId();
		this.name = product.getName();
		this.price = product.getPrice();

		this.thumbnailImage = product.getImages().isEmpty() ? null
			: product.getImages().get(0).getImageUrl();
	}
}
