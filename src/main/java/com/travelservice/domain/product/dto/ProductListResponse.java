package com.travelservice.domain.product.dto;

import com.travelservice.domain.product.entity.Product;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductListResponse {

	private Long productId;
	private String name;
	private Integer price;
	private String thumbnailImage;

	public ProductListResponse(Product product) {
		this.productId = product.getProductId();
		this.name = product.getName();
		this.price = product.getPrice();

		this.thumbnailImage = product.getImages().isEmpty() ? null
			: product.getImages().get(0).getImageUrl();
	}
}
