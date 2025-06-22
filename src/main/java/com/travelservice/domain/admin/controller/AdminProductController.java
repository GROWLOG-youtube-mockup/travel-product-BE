package com.travelservice.domain.admin.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.admin.service.AdminProductService;
import com.travelservice.domain.product.dto.AddProductRequest;
import com.travelservice.domain.product.dto.ProductDetailResponse;
import com.travelservice.domain.product.dto.UpdateProductRequest;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.service.ProductServiceImpl;
import com.travelservice.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Tag(name = "관리자 상품 조회")
public class AdminProductController {

	private final AdminProductService productService;

	private final ProductServiceImpl productServiceImpl;

	@Operation(summary = "상품 추가")
	@PostMapping
	public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(
		@RequestBody AddProductRequest request) {

		ProductDetailResponse response = productServiceImpl.createProduct(request);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.ok(response));
	}

	@GetMapping
	@Operation(summary = "관리자용 상품 목록 조회")
	public AdminProductService.PagedAdminProductListResponse getAdminProductList(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) Long regionId
	) {
		return productService.getAdminProductList(PageRequest.of(page - 1, size), regionId);
	}

	@Operation(summary = "상품 상세 조회")
	@GetMapping("/{productId}")
	public ApiResponse<ApiResponse<ProductDetailResponse>> getProductById(@PathVariable Long productId) {
		ProductDetailResponse response = productServiceImpl.getProductDetail(productId);
		return ApiResponse.ok(ApiResponse.ok(response));
	}

	@Operation(summary = "상품 정보 수정")
	@PutMapping("/{productId}")
	public ApiResponse<ApiResponse<ProductDetailResponse>> updateProduct(@PathVariable Long productId,
		@RequestBody UpdateProductRequest request) {
		Product updatedProduct = productServiceImpl.updateProduct(productId, request);

		return ApiResponse.ok(ApiResponse.ok(ProductDetailResponse.from(updatedProduct)));
	}

	@Operation(summary = "상품 삭제")
	@DeleteMapping("/{productId}")
	public ApiResponse<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
		productServiceImpl.deleteProduct(productId);
		return ApiResponse.ok(ApiResponse.ok(null));
	}
}
