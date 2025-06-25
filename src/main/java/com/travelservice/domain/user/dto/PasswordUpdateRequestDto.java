package com.travelservice.domain.user.dto;

import lombok.Getter;

@Getter
public class PasswordUpdateRequestDto {
	private String currentPassword;
	private String newPassword;
}
