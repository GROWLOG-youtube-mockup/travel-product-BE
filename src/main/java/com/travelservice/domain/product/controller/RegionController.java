package com.travelservice.domain.product.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.product.dto.RegionResponse;
import com.travelservice.domain.product.service.RegionService;
import com.travelservice.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "지역 API")
@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
public class RegionController {

	private final RegionService regionService;

	@Operation(summary = "level로 지역 조회",
		description = "level=1: 광역시/도, level=2: 자치시")
	@GetMapping
	public ResponseEntity<ApiResponse<List<RegionResponse>>> getRegions(
		@RequestParam(value = "level", required = false) Integer level
	) {
		List<RegionResponse> regions = regionService.getRegionsByLevel(level);
		return ResponseEntity.ok(ApiResponse.ok(regions));
	}
}
