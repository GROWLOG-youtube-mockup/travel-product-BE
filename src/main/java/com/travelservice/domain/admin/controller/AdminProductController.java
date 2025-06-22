package com.travelservice.domain.admin.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.admin.service.AdminProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Tag(name = "관리자 상품 조회")
public class AdminProductController {

	private final AdminProductService productService;

	@GetMapping
	@Operation(summary = "관리자용 상품 목록 조회")
	public AdminProductService.PagedAdminProductListResponse getAdminProductList(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) Long regionId
	) {
		return productService.getAdminProductList(PageRequest.of(page - 1, size), regionId);
	}
}
