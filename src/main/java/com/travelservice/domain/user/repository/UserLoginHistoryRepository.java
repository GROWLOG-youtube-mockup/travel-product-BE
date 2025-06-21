package com.travelservice.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.user.entity.UserLoginHistory;

public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long> {
	List<UserLoginHistory> findByUser_UserId(Long userId);
}
