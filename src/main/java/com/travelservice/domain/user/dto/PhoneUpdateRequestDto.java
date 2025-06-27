package com.travelservice.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PhoneUpdateRequestDto {

	@Schema(description = "사용자 전화번호", example = "01012345678")
	private String phoneNumber;
}
