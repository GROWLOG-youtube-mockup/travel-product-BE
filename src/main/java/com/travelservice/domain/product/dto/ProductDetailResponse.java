package com.travelservice.domain.product.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.ProductImage;

import lombok.Getter;

@Getter
public class ProductDetailResponse {

	private final Integer productId;
	private final String name;
	private final Integer price;
	private final Integer totalQuantity;
	private final Integer stockQuantity;
	private final String description;
	private final Integer saleStatus;
	private final Integer type;
	private final Integer duration;
	private final RegionResponse region;
	private final List<String> imageUrls;

	public ProductDetailResponse(Product product) {
		this.productId = product.getProductId();
		this.name = product.getName();
		this.price = product.getPrice();
		this.totalQuantity = product.getTotalQuantity();
		this.stockQuantity = product.getStockQuantity();
		this.description = product.getDescription();
		this.saleStatus = product.getSaleStatus();
		this.type = product.getType();
		this.duration = product.getDuration();
		this.region = product.getProductId() != null ? new RegionResponse(product.getRegion()) : null;
		this.imageUrls = product.getImages() != null ? product.getImages().stream()
			.map(ProductImage::getImageUrl)
			.collect(Collectors.toList())
			: List.of();
	}
}
