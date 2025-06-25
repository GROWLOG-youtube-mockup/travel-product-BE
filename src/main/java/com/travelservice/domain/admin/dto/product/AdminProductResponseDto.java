package com.travelservice.domain.admin.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminProductResponseDto {
	private Long productId;
	private String thumbnail;
	private String name;
	private RegionDto region;
	private Integer type;
	private Integer saleStatus;
	private Integer price;
	private Integer stockQuantity;
	private Integer duration;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class RegionDto {
		private Integer regionId;
		private String name;
	}
}
