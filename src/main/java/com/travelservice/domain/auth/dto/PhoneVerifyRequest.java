package com.travelservice.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "휴대폰 인증 확인 DTO - 휴대폰 번호와 인증 코드를 통해 인증 확인 요청")
public class PhoneVerifyRequest {

	@Schema(description = "휴대폰 번호", example = "01012345678")
	private String phoneNumber;

	@Schema(description = "인증 코드", example = "123456")
	private String code;
}
