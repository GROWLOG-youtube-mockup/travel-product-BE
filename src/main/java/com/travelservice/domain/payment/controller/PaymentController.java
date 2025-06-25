package com.travelservice.domain.payment.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.order.service.OrderService;
import com.travelservice.domain.payment.dto.PaymentApproveRequestDto;
import com.travelservice.domain.payment.dto.PaymentResponseDto;
import com.travelservice.domain.payment.entity.Payment;
import com.travelservice.domain.payment.service.PaymentService;
import com.travelservice.domain.user.entity.User;
import com.travelservice.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
	private final PaymentService paymentService;
	private final OrderService orderService;

	@Operation(
		summary = "Toss 결제 승인 처리",
		description = "Toss 결제 성공 후, 프론트에서 전달받은 paymentKey, orderId, amount 값을 기반으로 결제를 최종 승인하고, 결제 상태를 'PAID'로 변경."
	)
	@PostMapping("/approve")
	public ResponseEntity<ApiResponse<PaymentResponseDto>> approve(@RequestBody PaymentApproveRequestDto dto)
		throws IOException {
		PaymentResponseDto response = paymentService.approve(dto);
		return ResponseEntity.ok(ApiResponse.ok(response));
	}

	@Operation(
		summary = "결제 상태 조회",
		description = "특정 주문(orderId)에 대한 결제 정보를 조회. 결제 금액, 결제 방식, 결제 일시 등 상세 정보를 반환."
	)
	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResponse<PaymentResponseDto>> getPaymentStatus(@PathVariable Long orderId,
		@AuthenticationPrincipal User user) {
		Payment payment = paymentService.getPaymentStatus(orderId, user);
		return ResponseEntity.ok(ApiResponse.ok(new PaymentResponseDto(payment)));
	}

	private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

	@Operation(
		summary = "Toss 결제 성공 Redirect 수신",
		description = "Toss 결제 완료 후 리디렉션 주소로 호출되며, "
			+ "paymentKey, orderId, amount를 확인. 테스트 또는 개발용으로 사용됨. 프론트 연동 시 프론트에서 리다이렉션 처리"
	)
	@GetMapping("/success")
	public String tossSuccess(
		@RequestParam String paymentKey,
		@RequestParam String orderId,
		@RequestParam Long amount
	) {
		log.info("Toss redirect 받음: paymentKey={}, orderId={}, amount={}", paymentKey, orderId, amount);

		return """
			Toss 결제 성공!<br>
			아래 값을 Postman에 복사해서 /payments/approve API 테스트 하세요.<br><br>
			paymentKey: %s<br>
			orderId: %s<br>
			amount: %d
			""".formatted(paymentKey, orderId, amount);
	}

	@Operation(summary = "결제 취소", description = "주문 ID를 기반으로 결제 및 주문을 취소.")
	@PostMapping("/cancel")
	public ResponseEntity<ApiResponse<String>> cancelPayment(@RequestParam Long orderId) {
		paymentService.cancel(orderId);
		return ResponseEntity.ok(ApiResponse.ok("결제가 취소되었습니다."));
	}

	/*
	@Operation(
		summary = "장바구니 항목 즉시 주문 + 결제",
		description = "장바구니에서 선택한 항목을 기반으로 주문을 생성하고, 즉시 결제까지 처리. Toss와의 연동은 테스트용이며, 추후 프론트 연동 시 분리할 수 있음."
	)
	@PostMapping("/from-cart/{cartItemId}")
	public ResponseEntity<ApiResponse<OrderResponseDto>> orderSingleCartItem(
		@PathVariable Long cartItemId,
		@AuthenticationPrincipal User user
	) {
		Order order = orderService.createOrderFromCartItem(user, cartItemId);

		Payment payment = paymentService.payNow(order);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.ok(new OrderResponseDto(order)));
	}*/

}
