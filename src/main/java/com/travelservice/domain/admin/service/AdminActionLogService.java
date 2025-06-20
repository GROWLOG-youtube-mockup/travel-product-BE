package com.travelservice.domain.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.actionlog.AdminActionLogDto;
import com.travelservice.domain.admin.dto.actionlog.PagedAdminActionLogDto;
import com.travelservice.domain.admin.entity.AdminActionLog;
import com.travelservice.domain.admin.repository.AdminActionLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminActionLogService {
	private final AdminActionLogRepository logRepository;

	public PagedAdminActionLogDto getLogs(int page, int size, Long userId, Integer actionType) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("timestamp").descending());
		Page<AdminActionLog> logPage;

		if (userId != null && actionType != null) {
			logPage = logRepository.findByActionTypeAndUser_UserId(actionType, userId, pageable);
		} else if (userId != null) {
			logPage = logRepository.findByUser_UserId(userId, pageable);
		} else if (actionType != null) {
			logPage = logRepository.findByActionType(actionType, pageable);
		} else {
			logPage = logRepository.findAll(pageable);
		}

		List<AdminActionLogDto> content = logPage.getContent().stream()
			.map(this::convertToDto)
			.collect(Collectors.toList());

		return PagedAdminActionLogDto.builder()
			.content(content)
			.totalElements(logPage.getTotalElements())
			.totalPages(logPage.getTotalPages())
			.currentPage(page)
			.build();
	}

	private AdminActionLogDto convertToDto(AdminActionLog log) {
		return AdminActionLogDto.builder()
			.logId(log.getLogId())
			.userId(log.getUser().getUserId())
			.actionType(log.getActionType())
			.targetId(log.getTargetId())
			.timestamp(log.getTimestamp().toString())
			.build();
	}
}
