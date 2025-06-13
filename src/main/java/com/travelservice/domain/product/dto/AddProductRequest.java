package com.travelservice.domain.product.dto;

import java.util.List;

import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.ProductImage;
import com.travelservice.domain.product.entity.Region;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddProductRequest {

	private String name;
	private Integer price;
	private Integer totalQuantity;
	private Integer stockQuantity;
	private String description;
	private Integer saleStatus;
	private Integer type;
	private Integer duration;
	private Integer regionId;
	private List<String> imageUrls;

	public Product toEntity(Region region) {
		Product product = Product.builder()
			.name(name)
			.price(price)
			.totalQuantity(totalQuantity)
			.stockQuantity(stockQuantity)
			.description(description)
			.saleStatus(saleStatus)
			.type(type)
			.duration(duration)
			.region(region)
			.build();

		// 이미지 Url을 ProductImage 엔티티로 변환하여 추가
		if (imageUrls != null) {
			imageUrls.forEach(url ->
				product.addImage(ProductImage.builder()
					.imageUrl(url)
					.build()
				)
			);
		}

		return product;
	}
}
