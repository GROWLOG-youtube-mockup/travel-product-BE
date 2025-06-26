package com.travelservice.domain.admin.dto.actionlog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminActionLogDto {
	private Long logId;
	private Long userId;
	@Schema(description = "0: 상품 등록, 1: 주문 상태 변경, 2: 사용자 관리")
	private int actionType;
	@Schema(description = "대상 ID")
	private Long targetId;
	@Schema(description = "액션 시간")
	private String timestamp;
}
