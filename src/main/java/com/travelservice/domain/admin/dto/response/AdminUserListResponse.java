package com.travelservice.domain.admin.dto.response;

import java.time.LocalDateTime;

public record AdminUserListResponse(
	Long userId,
	String name,
	String email,
	String phoneNumber,
	int roleCode,
	LocalDateTime createdAt,
	LocalDateTime deletedAt
) {}
