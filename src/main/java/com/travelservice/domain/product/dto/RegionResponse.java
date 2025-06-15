package com.travelservice.domain.product.dto;

import com.travelservice.domain.product.entity.Region;

import lombok.Getter;

@Getter
public class RegionResponse {

	private Long regionId;
	private String parentName;
	private String name;

	public RegionResponse(Region region) {

		this.regionId = region.getRegionId();
		this.parentName = region.getParent().getName();
		this.name = region.getName();
	}
}
