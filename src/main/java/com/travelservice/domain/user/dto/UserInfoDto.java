package com.travelservice.domain.user.dto;

import com.travelservice.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserInfoDto(
	@Schema(description = "사용자 ID", example = "1")
	Long userId,

	@Schema(description = "사용자 이름", example = "홍길동")
	String name,

	@Schema(description = "이메일 주소", example = "hong@example.com")
	String email,

	@Schema(description = "휴대폰 번호", example = "01012345678")
	String phoneNumber,

	@Schema(description = "역할 코드 ( 0 = 사용자, 1 = 관리자, 2 = 최고관리자)", example = "0")
	int roleCode
) {
	public static UserInfoDto from(User user) {
		return new UserInfoDto(
			user.getUserId(),
			user.getName(),
			user.getEmail(),
			user.getPhoneNumber(),
			user.getRoleCode()
		);
	}
}
