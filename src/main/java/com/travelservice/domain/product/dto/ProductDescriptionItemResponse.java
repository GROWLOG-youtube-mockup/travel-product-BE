package com.travelservice.domain.product.dto;

import com.travelservice.domain.product.entity.ProductDescriptionItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDescriptionItemResponse {

	private Long itemId;
	private String content;
	private Integer sortOrder;

	public static ProductDescriptionItemResponse from(ProductDescriptionItem item) {
		return ProductDescriptionItemResponse.builder()
			.itemId(item.getItemId())
			.content(item.getContent())
			.sortOrder(item.getSortOrder())
			.build();
	}
}
