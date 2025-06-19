package com.travelservice.domain.admin.dto;

import java.time.LocalDateTime;

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
	private String status;
	private Integer totalQuantity;
	private LocalDateTime orderDate;
	private LocalDateTime cancelDate;
	private LocalDateTime updatedAt; // 상태변경일자
}
