package com.travelservice.domain.product.dto;

import com.travelservice.domain.product.entity.ProductDescriptionGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDescriptionGroupResponse {
	private Long groupId;
	private String title;
	private Integer type;
	private Integer sortOrder;
	// private List<ProductDescriptionItemResponse> items;

	public static ProductDescriptionGroupResponse from(ProductDescriptionGroup group) {
		return ProductDescriptionGroupResponse.builder()
			.groupId(group.getGroupId())
			.title(group.getTitle())
			.type(group.getType())
			.sortOrder(group.getSortOrder())
			.build();
	}
}
