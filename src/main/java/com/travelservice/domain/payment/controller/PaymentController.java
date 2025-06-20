package com.travelservice.domain.payment.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelservice.domain.payment.dto.PaymentApproveRequestDto;
import com.travelservice.domain.payment.dto.PaymentResponseDto;
import com.travelservice.domain.payment.service.PaymentService;
import com.travelservice.global.common.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
	private final PaymentService paymentService;

	@PostMapping("/approve")
	public ResponseEntity<ApiResponse<PaymentResponseDto>> approve(@RequestBody PaymentApproveRequestDto dto)
			throws IOException {
		PaymentResponseDto response = paymentService.approve(dto);
		return ResponseEntity.ok(ApiResponse.ok(response));
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResponse<PaymentResponseDto>> getPaymentStatus(@PathVariable Long orderId) {
		PaymentResponseDto response = paymentService.getPaymentStatusByOrderId(orderId);
		return ResponseEntity.ok(ApiResponse.ok(response));
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
