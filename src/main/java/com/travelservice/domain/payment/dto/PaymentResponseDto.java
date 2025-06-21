package com.travelservice.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.travelservice.domain.payment.entity.Payment;

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
public class PaymentResponseDto {
	@JsonProperty("payment_id")
	private Long paymentId;
	private String status;
	private String method;
	@JsonProperty("paid_at")
	private String paidAt;

	public PaymentResponseDto(Payment payment) {
		this.paymentId = payment.getPaymentId();
		this.status = payment.getStatus().name(); // enum이면 .name() or .toString()
		this.method = payment.getMethod(); // 필요 시 null 체크
		this.paidAt = payment.getPaidAt().toString(); // LocalDateTime일 경우 toString or format()
	}
}
