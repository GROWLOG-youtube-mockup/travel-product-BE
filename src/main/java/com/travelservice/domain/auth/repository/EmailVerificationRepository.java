package com.travelservice.domain.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.auth.entity.EmailVerification;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
	boolean existsByEmailAndVerifiedTrue(String email);

	Optional<EmailVerification> findByEmail(String email);
}
