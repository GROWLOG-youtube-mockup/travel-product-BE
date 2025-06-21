package com.travelservice.domain.payment.controller;

import java.io.IOException;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelservice.domain.order.dto.OrderResponseDto;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.service.OrderService;
import com.travelservice.domain.payment.dto.PaymentApproveRequestDto;
import com.travelservice.domain.payment.dto.PaymentResponseDto;
import com.travelservice.domain.payment.entity.Payment;
import com.travelservice.domain.payment.service.PaymentService;
import com.travelservice.domain.user.entity.User;
import com.travelservice.global.common.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
	private final PaymentService paymentService;
	private final OrderService orderService;

	@PostMapping("/approve")
	public ResponseEntity<ApiResponse<PaymentResponseDto>> approve(@RequestBody PaymentApproveRequestDto dto)
			throws IOException {
		PaymentResponseDto response = paymentService.approve(dto);
		return ResponseEntity.ok(ApiResponse.ok(response));
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResponse<PaymentResponseDto>> getPaymentStatus(@PathVariable Long orderId, @AuthenticationPrincipal User user) {
		Payment payment = paymentService.getPaymentStatus(orderId, user);
		return ResponseEntity.ok(ApiResponse.ok(new PaymentResponseDto(payment)));
	}

	@PostMapping("/from-cart/{cartItemId}")
	public ResponseEntity<ApiResponse<OrderResponseDto>> orderSingleCartItem(
		@PathVariable Long cartItemId,
		@AuthenticationPrincipal User user
	) {
		Order order = orderService.createOrderFromCartItem(user, cartItemId);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.ok(new OrderResponseDto(order)));
	}

	private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

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
}
