package com.travelservice.domain.admin.dto.actionlog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminActionLogDto {
	private Long logId;
	private Long userId;
	private int actionType;
	private Long targetId;
	private String timestamp;
}
