package com.travelservice.domain.user.dto;

import com.travelservice.domain.user.entity.User;

public record UserInfoDto(
	Long userId,
	String name,
	String email,
	String phoneNumber
) {
	public static UserInfoDto from(User user) {
		return new UserInfoDto(
			user.getUserId(),
			user.getName(),
			user.getEmail(),
			user.getPhoneNumber()
		);
	}

}
