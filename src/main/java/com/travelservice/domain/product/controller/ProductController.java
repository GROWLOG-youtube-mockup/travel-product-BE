package com.travelservice.domain.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.product.dto.AddProductRequest;
import com.travelservice.domain.product.dto.ProductDetailResponse;
import com.travelservice.domain.product.dto.ProductListResponse;
import com.travelservice.domain.product.dto.UpdateProductRequest;
import com.travelservice.domain.product.entity.Product;
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

	@Operation(summary = "상품 추가")
	@PostMapping
	public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(
		@RequestBody AddProductRequest request) {

		ProductDetailResponse response = productServiceImpl.createProduct(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
	}

	@Operation(summary = "상품 목록 조회", description = "전체 상품을 조회합니다")
	@GetMapping
	public ResponseEntity<ApiResponse<List<ProductListResponse>>> getAllProducts() {
		List<ProductListResponse> products = productServiceImpl.getAllProducts();
		return ResponseEntity.ok(ApiResponse.ok(products));
	}

	@Operation(summary = "상품 상세 조회")
	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(@PathVariable Long productId) {
		ProductDetailResponse response = productServiceImpl.getProductDetail(productId);
		return ResponseEntity.ok(ApiResponse.ok(response));
	}

	@Operation(summary = "상품 정보 수정")
	@PutMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProduct(@PathVariable Long productId,
		@RequestBody UpdateProductRequest request) {
		Product updatedProduct = productServiceImpl.updateProduct(productId, request);

		return ResponseEntity.ok(ApiResponse.ok(ProductDetailResponse.from(updatedProduct)));
	}

	@Operation(summary = "상품 삭제")
	@DeleteMapping("/{productId}")
	public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
		productServiceImpl.deleteProduct(productId);
		return ResponseEntity.ok(ApiResponse.ok(null));
	}
}
