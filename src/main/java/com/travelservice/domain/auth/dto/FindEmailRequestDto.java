package com.travelservice.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindEmailRequestDto {
	private String name;
	private String phoneNumber;

	// sms 인증 방식 도입 시 주석 해제(현재 사용하지 않음)
	//private String code;
}
