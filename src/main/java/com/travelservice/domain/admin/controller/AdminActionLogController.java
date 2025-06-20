package com.travelservice.domain.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.admin.dto.actionlog.PagedAdminActionLogDto;
import com.travelservice.domain.admin.service.AdminActionLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
@Tag(name = "관리자 액션 로그")
public class AdminActionLogController {

	private final AdminActionLogService logService;

	@GetMapping
	@Operation(summary = "관리자 액션 로그 조회")
	public ResponseEntity<PagedAdminActionLogDto> getLogs(
		@RequestParam(defaultValue = "1") Integer page,
		@RequestParam(defaultValue = "10") Integer size,
		@RequestParam(value = "user_id", required = false) Long userId,
		@RequestParam(value = "actionType", required = false) Integer actionType
	) {
		PagedAdminActionLogDto result = logService.getLogs(page, size, userId, actionType);
		return ResponseEntity.ok(result);
	}
}
