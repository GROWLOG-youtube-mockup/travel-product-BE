package com.travelservice.domain.admin.dto.order;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminOrderResponseDto {
	private Long orderId;
	private Long userId;
	private String userName;
	private String userEmail;
	@Schema(description = "주문 상태. (PENDING, PAID, CANCELLED) ")
	private String status;
	@Schema(description = "상품 수를 의미합니다.(상품 수=인원)")
	private Integer peopleCount;
	@Schema(description = "주문 일자")
	private LocalDateTime orderDate;
	@Schema(description = "취소 일자(유효한 주문이라면 null값)")
	private LocalDateTime cancelDate;
	@Schema(description = "상태 변경 일자")
	private LocalDateTime updatedAt;
}
