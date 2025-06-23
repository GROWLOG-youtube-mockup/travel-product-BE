package com.travelservice.domain.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.travelservice.domain.user.entity.User;

@Repository
public interface AdminUserRepository extends JpaRepository<User, Long> {

	// 전체 사용자 조회
	@Query("SELECT u FROM User u WHERE u.deletedAt IS NULL ORDER BY u.createdAt DESC")
	Page<User> findActiveUsers(Pageable pageable);

	// 역할 코드별 사용자 조회 (관리자 조회)
	@Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND u.roleCode IN :roleCodes ORDER BY u.createdAt DESC")
	Page<User> findActiveUsersByRoleCodes(@Param("roleCodes") Integer roleCodes, Pageable pageable);

	// 사용자 삭제
	@Modifying
	@Query("UPDATE User u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.userId = :userId")
	int softDeleteById(@Param("userId") Long userId);

}
