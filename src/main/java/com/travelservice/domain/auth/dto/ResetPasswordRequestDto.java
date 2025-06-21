package com.travelservice.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDto {
	private String name;
	private String phoneNumber;
	private String email;
}
