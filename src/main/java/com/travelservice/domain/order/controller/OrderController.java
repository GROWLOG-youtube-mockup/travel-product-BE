package com.travelservice.domain.order.controller;

import java.io.IOException;
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

import com.travelservice.domain.order.dto.OrderAndPaymentRequestDto;
import com.travelservice.domain.order.dto.OrderRequestDto;
import com.travelservice.domain.order.dto.OrderResponseDto;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.service.OrderService;
import com.travelservice.domain.payment.dto.PaymentApproveRequestDto;
import com.travelservice.domain.payment.dto.PaymentResponseDto;
import com.travelservice.domain.payment.service.PaymentService;
import com.travelservice.domain.user.entity.User;
import com.travelservice.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
	private final OrderService orderService;
	private final PaymentService paymentService;

	@Operation(
		summary = "예약 + 결제 통합 처리 (상품 상세 or 장바구니)",
		description = "상품 ID, 날짜, 인원 수를 입력받아 예약을 생성하고 Toss 결제까지 한 번에 처리. 상품 상세 페이지와 장바구니 모두에서 사용."
	)
	@PostMapping("/pay")
	public ResponseEntity<ApiResponse<PaymentResponseDto>> payDirectly(
		@RequestBody OrderAndPaymentRequestDto dto,
		@AuthenticationPrincipal User user
	) throws IOException {
		Order order = orderService.createOrder(user.getEmail(), dto.getItems());

		PaymentApproveRequestDto approveDto = PaymentApproveRequestDto.builder()
			.paymentKey(dto.getPayment().getPaymentKey())
			.orderId(order.getOrderId())
			.amount(dto.getPayment().getAmount())
			.build();

		PaymentResponseDto paymentResponse = paymentService.approve(approveDto);

		return ResponseEntity.ok(ApiResponse.ok(paymentResponse));
	}

	/*
	@Operation(
		summary = "예약만 생성 (결제 없음)",
		description = "상품, 날짜, 인원 수를 입력받아 예약(주문)을 먼저 생성하고, 결제는 이후 Toss API를 통해 별도로 처리. 견적 저장, 관리자 승인 등이 필요한 경우 사용."
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
	*/

	/*
	@Operation(
		summary = "장바구니 다중 항목 예약 생성 (미사용)",
		description = "장바구니에서 여러 항목을 선택하여 한 번에 예약을 생성하는 API. 현재 UI에서는 다중 선택을 지원하지 않아 사용되지 않음."
	)
	@PostMapping("/from-cart")
	public ResponseEntity<ApiResponse<OrderResponseDto>> orderFromCart(@RequestParam String email) {
		Order order = orderService.createOrderFromCart(email);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.ok(new OrderResponseDto(order)));
	}
	*/

	@Operation(
		summary = "예약 상세 조회",
		description = "로그인된 사용자가 자신의 특정 예약(주문)을 상세 조회. 여행 상품명, 시작일, 인원 수, 결제 상태 등을 포함."
	)
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(
		@PathVariable Long id,
		@AuthenticationPrincipal User user
	) {
		Order order = orderService.findByIdAndUser(id, user);
		return ResponseEntity.ok(ApiResponse.ok(new OrderResponseDto(order)));
	}

	@Operation(
		summary = "내 전체 예약 목록 조회",
		description = "현재 로그인된 사용자의 모든 예약(주문) 내역을 조회. 예약별로 여행 상품명, 상태, 시작일 등의 정보를 제공."
	)
	@GetMapping
	public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getMyOrders(
		@AuthenticationPrincipal User user
	) {
		List<Order> orders = orderService.findByUser(user);
		List<OrderResponseDto> result = orders.stream()
			.map(OrderResponseDto::new)
			.toList();
		return ResponseEntity.ok(ApiResponse.ok(result));
	}
}
