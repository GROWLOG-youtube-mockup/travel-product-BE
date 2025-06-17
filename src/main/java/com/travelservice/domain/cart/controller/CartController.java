package com.travelservice.domain.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.cart.dto.AddToCartRequest;
import com.travelservice.domain.cart.service.CartService;
import com.travelservice.domain.user.entity.User;
import com.travelservice.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "장바구니 API", description = "장바구니 상품 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

	private final CartService cartService;

	@Operation(summary = "장바구니에 상품 추가")
	@PostMapping
	public ResponseEntity<ApiResponse<Void>> addToCart(@RequestBody AddToCartRequest request,
		@AuthenticationPrincipal User user) {
		cartService.addToCart(user, request);
		return ResponseEntity.ok(ApiResponse.ok(null));
	}

}
