package com.travelservice.domain.admin.dto.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
	private Long userId;
	private String name;
	private String email;
	private String phoneNumber;
	private Integer roleCode;
	private LocalDateTime createAt;
	private LocalDateTime deleteAt;
}
