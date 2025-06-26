package com.travelservice.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "이메일 찾기 요청 DTO - 이름과 휴대폰 번호를 통해 이메일 찾기 요청")
public class FindEmailRequestDto {

	@Schema(description = "이름", example = "홍길동")
	private String name;

	@Schema(description = "휴대폰 번호", example = "01012345678")
	private String phoneNumber;

	// sms 인증 방식 도입 시 주석 해제(현재 사용하지 않음)
	//private String code;
}
