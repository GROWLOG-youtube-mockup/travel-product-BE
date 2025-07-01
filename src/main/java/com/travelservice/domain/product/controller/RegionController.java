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

	@Operation(summary = "지역 조회/필터링",
		description = """
			조건에 따라 지역 목록을 동적으로 조회함.
			- **level**: 'level=1' 광역시/도, 'level=2' 자치시
			- **parentId**: 특정 부모 지역에 속한 하위 지역 목록 조회
			- **파라미터 없음**: 전체 지역 목록 조회
			- level과 parentId 동시에 주어질 경우 parentId 우선 적용
			""")
	@GetMapping
	public ResponseEntity<ApiResponse<List<RegionResponse>>> getRegions(
		@RequestParam(value = "level", required = false) Integer level,
		@RequestParam(value = "parentId", required = false) Long parentId
	) {
		List<RegionResponse> regions = regionService.getRegions(level, parentId);
		return ResponseEntity.ok(ApiResponse.ok(regions));
	}
}
