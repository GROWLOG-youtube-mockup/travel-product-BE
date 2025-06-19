package com.travelservice.domain.admin.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {
	private String name;
	private String email;
	private String phoneNumber;
	private Integer roleCode;
}
