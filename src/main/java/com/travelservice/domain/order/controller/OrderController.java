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
import com.travelservice.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
	private final OrderService orderService;

	@Operation(
		summary = "단건 예약 생성 (결제는 별도 진행)",
		description = "장바구니에서 하나의 항목을 선택하여 예약을 생성. 결제는 toss API에서 별도로 처리."
	)
	@PostMapping
	public ResponseEntity<ApiResponse<OrderResponseDto>> creatOrder(
		@Valid @RequestBody OrderRequestDto dto,
		@RequestParam String email
	) {
		Order order = orderService.createOrder(email, dto.getItems());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.ok(new OrderResponseDto(order)));
	}

	@Operation(
		summary = "장바구니 항목 예약 생성 ",
		description = "장바구니에서 상품을 선택하여 예약을 생성하는 API."
	)
	@PostMapping("/from-cart/{cartItemId}")
	public ResponseEntity<ApiResponse<OrderResponseDto>> orderFromCart(
		@PathVariable Long cartItemId,
		@RequestParam String email
	) {
		Order order = orderService.createOrderFromCart(email, cartItemId);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.ok(new OrderResponseDto(order)));
	}

	@Operation(
		summary = "예약 상세 조회",
		description = "로그인된 사용자가 자신의 특정 예약(주문)을 상세 조회. 여행 상품명, 시작일, 인원 수, 결제 상태 등을 포함."
	)
	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(
		@PathVariable Long orderId,
		@AuthenticationPrincipal(expression = "userId") Long userId
	) {
		//log.info("요청 들어옴 - user: {}", user);
		Order order = orderService.findByIdAndUser(orderId, userId);
		OrderResponseDto responseDto = OrderResponseDto.withItems(order);
		return ResponseEntity.ok(ApiResponse.ok(responseDto));
	}

	@Operation(
		summary = "내 전체 예약 목록 조회",
		description = "현재 로그인된 사용자의 모든 예약(주문) 내역을 조회. 예약별로 여행 상품명, 상태, 시작일 등의 정보를 제공."
	)
	@GetMapping
	public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getMyOrders(
		@AuthenticationPrincipal(expression = "userId") Long userId
	) {
		log.info("요청 들어옴 - user: {}", userId); // 추가
		List<OrderResponseDto> orders = orderService.getOrders(userId);
		return ResponseEntity.ok(ApiResponse.ok(orders));
	}
}
