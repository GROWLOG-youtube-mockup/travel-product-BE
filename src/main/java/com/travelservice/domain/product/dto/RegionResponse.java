package com.travelservice.domain.product.dto;

import com.travelservice.domain.product.entity.Region;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RegionResponse {

	private Long regionId;
	private String name;
	private Long parentId;

	public static RegionResponse from(Region region) {
		return RegionResponse.builder()
			.regionId(region.getRegionId())
			.name(region.getName())
			.parentId(region.getParent().getRegionId())
			.build();
	}
}
