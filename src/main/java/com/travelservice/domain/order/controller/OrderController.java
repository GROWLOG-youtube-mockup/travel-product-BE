package com.travelservice.domain.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.order.dto.OrderRequestDto;
import com.travelservice.domain.order.dto.OrderResponseDto;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.service.OrderService;
import com.travelservice.domain.user.entity.User;
import com.travelservice.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
	private final OrderService orderService;

	@Operation(
		summary = "상품 상세 페이지에서 직접 주문 생성",
		description = "상품 상세 페이지에서 여행 상품, 날짜, 인원 수를 선택하여 주문을 생성. 결제는 Toss 결제창을 통해 별도로 진행."
	)
	@PostMapping
	public ResponseEntity<ApiResponse<OrderResponseDto>> creatOrder(
			@RequestBody OrderRequestDto dto,
			@RequestParam String email
	) {
		Order order = orderService.createOrder(email, dto.getItems());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.ok(new OrderResponseDto(order)));
	}

	/*
	
	@Operation(
		summary = "장바구니 다중 항목으로 주문 생성 (현재 미사용)",
		description = "장바구니에서 여러 개의 항목을 선택하여 한 번에 주문을 생성하는 API. 현재 UI에서는 다중 선택을 지원하지 않아 사용되지 않음."
	)
	@PostMapping("/from-cart")
	public ResponseEntity<ApiResponse<OrderResponseDto>> orderFromCart(@RequestParam String email) {
		Order order = orderService.createOrderFromCart(email);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.ok(new OrderResponseDto(order)));
	}*/

	@Operation(
		summary = "주문 상세 조회",
		description = "로그인된 사용자가 자신의 특정 주문을 상세 조회. 해당 주문의 여행 상품, 인원 수, 시작일, 결제 상태 등의 정보를 포함."
	)
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(@PathVariable Long id, @AuthenticationPrincipal User user) {
		Order order = orderService.findByIdAndUser(id, user);
		return ResponseEntity.ok(ApiResponse.ok(new OrderResponseDto(order)));
	}

	@Operation(
		summary = "내 주문 전체 목록 조회",
		description = "현재 로그인된 사용자의 모든 주문 목록을 조회. 주문별로 상품명, 시작일, 상태 등의 정보를 제공."
	)
	@GetMapping
	public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getMyOrders(@AuthenticationPrincipal User user) {
		List<Order> orders = orderService.findByUser(user);
		List<OrderResponseDto> result = orders.stream()
				.map(OrderResponseDto::new)
				.toList();
		return ResponseEntity.ok(ApiResponse.ok(result));
	}
}
