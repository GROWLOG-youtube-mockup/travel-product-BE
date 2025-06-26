package com.travelservice.domain.user.dto;

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
public class UserRegistrationRequestDto {
	@Schema(description = "사용자 이름", example = "홍길동")
	private String username;

	@Schema(description = "휴대폰 번호", example = "01012345678")
	private String phoneNumber;

	@Schema(description = "이메일 주소", example = "hong@example.com")
	private String email;

	@Schema(description = "비밀번호", example = "securePassword123")
	private String password;
}
