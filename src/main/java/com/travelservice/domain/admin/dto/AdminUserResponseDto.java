package com.travelservice.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponseDto {
	private Long userId;
	private String name;
	private String email;
	private Integer roleCode;
}
