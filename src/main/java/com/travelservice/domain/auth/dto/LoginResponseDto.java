package com.travelservice.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
	private Long userId;
	private String name;
	private String accessToken;
}
