package com.travelservice.domain.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailResponseDto {
	private Long userId;
	private String name;
	private String email;
	private String phoneNumber;
	private Integer roleCode;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;
	private List<LoginHistoryDto> loginHistory;
	private Long orderCount;
	private Long totalSpent;
}
