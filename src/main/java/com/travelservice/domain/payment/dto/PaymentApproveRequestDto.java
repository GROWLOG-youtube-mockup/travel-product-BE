package com.travelservice.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Schema(description = "결제 승인 요청 DTO - 결제 승인 처리에 필요한 결제 정보 전달")
public class PaymentApproveRequestDto {

	@Schema(description = "결제 키 (PG사에서 발급한 고유 식별자)", example = "toss-generated-key-12345")
	@JsonProperty("payment_key")
	private String paymentKey;

	@Schema(description = "주문 ID", example = "1001")
	@JsonProperty("order_id")
	private Long orderId;

	@Schema(description = "결제 금액", example = "299000")
	private int amount;

	@Schema(description = "결제 대행사 코드 (toss)", example = "toss")
	@JsonProperty("payment_gateway")
	private String paymentGateway;

	@Schema(description = "프론트엔드 트랜잭션 식별자 (옵션)", example = "tx-001")
	@JsonProperty("transaction_id")
	private String transactionId;
}
