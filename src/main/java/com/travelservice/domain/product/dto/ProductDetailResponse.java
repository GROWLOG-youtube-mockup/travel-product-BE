package com.travelservice.domain.product.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.ProductImage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
	private Long productId;
	private String name;
	private Integer price;
	private Integer totalQuantity;
	private Integer stockQuantity;
	private String description;
	private Integer saleStatus;
	private Integer type;
	private Integer duration;
	private RegionResponse region;
	private List<String> imageUrls;
	private List<ProductDescriptionGroupResponse> descriptionGroups;

	public static ProductDetailResponse from(Product product) {
		return ProductDetailResponse.builder()
			.productId(product.getProductId())
			.name(product.getName())
			.price(product.getPrice())
			.totalQuantity(product.getTotalQuantity())
			.stockQuantity(product.getStockQuantity())
			.description(product.getDescription())
			.saleStatus(product.getSaleStatus())
			.type(product.getType())
			.duration(product.getDuration())
			.region(RegionResponse.from(product.getRegion()))
			.imageUrls(product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList()))
			.descriptionGroups(product.getDescriptionGroups().stream().map(ProductDescriptionGroupResponse::from)
				.collect(Collectors.toList()))
			.build();
	}
}
