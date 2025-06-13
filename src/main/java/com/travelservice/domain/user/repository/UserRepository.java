package com.travelservice.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);
}
