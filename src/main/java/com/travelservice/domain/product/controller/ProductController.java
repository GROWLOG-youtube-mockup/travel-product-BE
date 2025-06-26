package com.travelservice.domain.product.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.product.dto.ProductDetailResponse;
import com.travelservice.domain.product.dto.ProductListResponse;
import com.travelservice.domain.product.service.ProductServiceImpl;
import com.travelservice.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "상품 API", description = "여행 상품 관련 API입니다")
@RestController  // JSON/XML 형태로 객체 데이터 반환
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductServiceImpl productServiceImpl;

	@Operation(summary = "상품 목록 조회",
		description = "전체 상품을 조회합니다 (지역, 태그 필터링 포함)"
					  + "stockQuantity: 잔여 수량 (totalQuantity - 판매된 상품 수"
					  + "tags: {title = tags}인 descriptionGroup의 item content 값")
	@GetMapping
	public ResponseEntity<ApiResponse<List<ProductListResponse>>> getAllProducts(
		@RequestParam(value = "regionId", required = false) Long regionId,
		@RequestParam(value = "parentRegionId", required = false) Long parentRegionId,
		@RequestParam(value = "tags", required = false) String tags
	) {
		List<ProductListResponse> products = productServiceImpl.getAllProducts(regionId, parentRegionId, tags);
		return ResponseEntity.ok(ApiResponse.ok(products));
	}

	@Operation(summary = "상품 상세 조회")
	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(@PathVariable Long productId) {
		ProductDetailResponse response = productServiceImpl.getProductDetail(productId);
		return ResponseEntity.ok(ApiResponse.ok(response));
	}
}
