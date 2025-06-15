package com.travelservice.domain.admin.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistoryDto {
	private LocalDateTime loginTime;
	private String ipAddress;
	private String userAgent;
}
