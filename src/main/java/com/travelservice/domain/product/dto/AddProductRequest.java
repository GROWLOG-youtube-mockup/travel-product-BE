package com.travelservice.domain.product.dto;

import com.travelservice.domain.product.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddProductRequest {

	private String name;
	private Integer price;
	private Integer totalQuantity;
	private Integer stockQuantity;
	private String description;
	private Integer saleStatus;
	private Integer type;
	private Integer duration;

	public Product toEntity() {
		return Product.builder()
			.name(name)
			.price(price)
			.totalQuantity(totalQuantity)
			.stockQuantity(stockQuantity)
			.description(description)
			.saleStatus(saleStatus)
			.type(type)
			.duration(duration)
			.build();
	}
}
