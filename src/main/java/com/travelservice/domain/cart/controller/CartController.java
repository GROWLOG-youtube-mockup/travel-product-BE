package com.travelservice.domain.cart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.cart.dto.AddToCartRequest;
import com.travelservice.domain.cart.dto.GetCartItemResponse;
import com.travelservice.domain.cart.service.CartService;
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

	@Operation(summary = "장바구니에 상품 추가",
		description = "quantity: 인원 수(= people count),"
			+ "startDate: 여행 시작일자")
	@PostMapping
	public ResponseEntity<ApiResponse<Void>> addToCart(@RequestBody AddToCartRequest request,
		@AuthenticationPrincipal(expression = "userId") Long userId) {
		cartService.addToCart(userId, request);
		return ResponseEntity.ok(ApiResponse.ok(null));
	}

	@Operation(summary = "장바구니 조회")
	@GetMapping
	public ResponseEntity<ApiResponse<List<GetCartItemResponse>>> getCartItems(
		@AuthenticationPrincipal(expression = "userId") Long userId) {
		List<GetCartItemResponse> cartItems = cartService.getCartItems(userId);
		return ResponseEntity.ok(ApiResponse.ok(cartItems));
	}

	@Operation(summary = "장바구니 상품 삭제")
	@DeleteMapping("/{selectedItemIdList}")
	public ResponseEntity<ApiResponse<Void>> deleteCartItem(@AuthenticationPrincipal(expression = "userId") Long userId,
		@PathVariable List<Long> selectedItemIdList) {
		cartService.deleteCartItems(userId, selectedItemIdList);
		return ResponseEntity.ok(ApiResponse.ok(null));
	}
}
