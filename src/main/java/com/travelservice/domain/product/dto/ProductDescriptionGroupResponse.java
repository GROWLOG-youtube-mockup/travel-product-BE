package com.travelservice.domain.product.dto;

import java.util.List;
import java.util.stream.Collectors;

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
	private List<ProductDescriptionItemResponse> items;

	public static ProductDescriptionGroupResponse from(ProductDescriptionGroup group) {
		return ProductDescriptionGroupResponse.builder()
			.groupId(group.getGroupId())
			.title(group.getTitle())
			.type(group.getType())
			.sortOrder(group.getSortOrder())
			.items(group.getDescriptionItems().stream()
				.map(ProductDescriptionItemResponse::from)
				.collect(Collectors.toList()))
			.build();
	}
}
