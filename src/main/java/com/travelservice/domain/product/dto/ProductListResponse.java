package com.travelservice.domain.product.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.ProductDescriptionItem;
import com.travelservice.domain.product.entity.ProductImage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponse {

	private Long productId;
	private String name;
	private List<String> imageUrls;
	private Integer price;
	private Integer stockQuantity;
	private Integer duration;
	private Integer saleStatus;
	private Integer type;
	private RegionResponse region;
	private List<String> tags;

	public static ProductListResponse from(Product product) {
		List<String> tags = product.getDescriptionGroups().stream()
			.filter(group -> "tags".equalsIgnoreCase(group.getTitle()))
			.findFirst()
			.map(group -> group.getDescriptionItems().stream()
				.map(ProductDescriptionItem::getContent)
				.collect(Collectors.toList()))
			.orElse(Collections.emptyList());

		return ProductListResponse.builder()
			.productId(product.getProductId())
			.name(product.getName())
			.imageUrls(product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList()))
			.price(product.getPrice())
			.stockQuantity(product.getStockQuantity())
			.duration(product.getDuration())
			.saleStatus(product.getSaleStatus())
			.type(product.getType())
			.region(RegionResponse.from(product.getRegion()))
			.tags(tags)
			.build();
	}
}
