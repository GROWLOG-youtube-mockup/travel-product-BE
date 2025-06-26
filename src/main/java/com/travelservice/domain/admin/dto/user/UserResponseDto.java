package com.travelservice.domain.admin.dto.user;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
	private Long userId;
	private String name;
	private String email;
	private String phoneNumber;
	@Schema(description = "사용자의 권한. (0: 사용자, 1: 관리자, 2: 최고관리자)")
	private Integer roleCode;
	@Schema(description = "생성 일자")
	private LocalDateTime createAt;
	@Schema(description = "탈퇴 일자")
	private LocalDateTime deleteAt;
}
