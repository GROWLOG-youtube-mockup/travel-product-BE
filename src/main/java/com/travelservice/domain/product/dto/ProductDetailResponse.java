package com.travelservice.domain.product.dto;

import com.travelservice.domain.product.entity.Product;

import lombok.Getter;

@Getter
public class ProductDetailResponse {

	private final String name;
	private final Integer price;
	private final Integer totalQuantity;
	private final Integer stockQuantity;
	private final String description;
	private final Integer saleStatus;
	private final Integer type;
	private final Integer duration;

	public ProductDetailResponse(Product product) {
		this.name = product.getName();
		this.price = product.getPrice();
		this.totalQuantity = product.getTotalQuantity();
		this.stockQuantity = product.getStockQuantity();
		this.description = product.getDescription();
		this.saleStatus = product.getSaleStatus();
		this.type = product.getType();
		this.duration = product.getDuration();
	}
}
