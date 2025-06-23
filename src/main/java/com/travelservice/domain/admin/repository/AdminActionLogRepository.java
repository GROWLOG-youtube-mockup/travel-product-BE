package com.travelservice.domain.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.admin.entity.AdminActionLog;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {
	Page<AdminActionLog> findByUser_UserId(Long userId, Pageable pageable);

	Page<AdminActionLog> findByActionType(int actionType, Pageable pageable);

	Page<AdminActionLog> findByActionTypeAndUser_UserId(int actionType, Long userId, Pageable pageable);
}
