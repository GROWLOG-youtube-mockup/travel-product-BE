package com.travelservice.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
	private Long userId;
	private String username;
	private String accessToken;
}
