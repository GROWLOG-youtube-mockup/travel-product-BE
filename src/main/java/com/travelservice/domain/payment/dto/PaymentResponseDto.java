package com.travelservice.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.travelservice.domain.payment.entity.Payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "결제 응답 DTO - 결제 승인 후 클라이언트에 반환되는 결제 정보")
public class PaymentResponseDto {

	@Schema(description = "결제 ID", example = "123456789")
	@JsonProperty("payment_id")
	private Long paymentId;

	@Schema(description = "결제 상태(PAID, FAILED, CANCELLED)", example = "PAID")
	private String status;

	@Schema(description = "결제 방법 (카드, 가상계좌, 계좌이체, 휴대폰)", example = "CARD")
	private String method;

	@Schema(description = "결제 완료 시간 (ISO 8601 형식)", example = "2025-08-01T12:34:56Z")
	@JsonProperty("paid_at")
	private String paidAt;

	public PaymentResponseDto(Payment payment) {
		this.paymentId = payment.getPaymentId();
		this.status = payment.getStatus().name(); // enum이면 .name() or .toString()
		this.method = payment.getMethod(); // 필요 시 null 체크
		this.paidAt = payment.getPaidAt().toString(); // LocalDateTime일 경우 toString or format()
	}
}
