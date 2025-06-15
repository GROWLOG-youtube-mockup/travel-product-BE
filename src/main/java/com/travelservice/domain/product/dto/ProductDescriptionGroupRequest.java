package com.travelservice.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDescriptionGroupRequest {
	private String title;
	private Integer type;
	private Integer sortOrder;
}
