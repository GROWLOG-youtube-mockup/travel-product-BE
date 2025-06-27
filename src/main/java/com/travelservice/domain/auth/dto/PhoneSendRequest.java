package com.travelservice.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "휴대폰 번호 전송 요청 DTO - 휴대폰 번호를 통한 인증 요청")
public class PhoneSendRequest {

	@Schema(description = "휴대폰 번호", example = "01012345678")
	private String phoneNumber;
}
