package com.travelservice.domain.product.dto;

import java.util.List;

import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.ProductImage;
import com.travelservice.domain.product.entity.Region;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateProductRequest {

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

	public void applyTo(Product product, Region region) {
		product.update(
			this.name,
			this.price,
			this.totalQuantity,
			this.stockQuantity,
			this.description,
			this.saleStatus,
			this.type,
			this.duration,
			region // region 객체 직접 주입
		);

		product.getImages().clear();
		if (imageUrls != null) {
			imageUrls.forEach(image -> product.addImage(ProductImage.builder().imageUrl(image).build()));
		}
	}
}
