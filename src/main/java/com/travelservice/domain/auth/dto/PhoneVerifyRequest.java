package com.travelservice.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhoneVerifyRequest {
	private String phoneNumber;
	private String code;
}
