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

	// 역할 코드별 사용자 조회 (필요하지 않다면 빼도 됨)
	@Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND u.roleCode = :roleCode ORDER BY u.createdAt DESC")
	Page<User> findActiveUsersByRoleCode(@Param("roleCode") Integer roleCode, Pageable pageable);

	// 사용자 정보 수정
	@Modifying
	@Query("UPDATE User u SET u.name = :name, u.email = :email, u.phoneNumber = :phoneNumber, u.roleCode = :roleCode WHERE u.userId = :userId")
	int updateUser(
		@Param("userId") Long userId,
		@Param("name") String name,
		@Param("email") String email,
		@Param("phoneNumber") String phoneNumber,
		@Param("roleCode") Integer roleCode
	);
}
