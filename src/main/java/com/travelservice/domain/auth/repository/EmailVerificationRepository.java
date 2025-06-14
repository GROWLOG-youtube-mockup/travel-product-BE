package com.travelservice.domain.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.auth.entity.EmailVerification;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
	boolean existsByEmailAndIsVerifiedTrue(String email);

	Optional<EmailVerification> findByEmail(String email);
}
