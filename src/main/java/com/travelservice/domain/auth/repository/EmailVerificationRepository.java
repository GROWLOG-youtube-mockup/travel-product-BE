package com.travelservice.domain.auth.repository;

import java.util.Optional;

import com.travelservice.domain.auth.entity.EmailVerification;

public interface EmailVerificationRepository {
	boolean existsByEmailAndVerifiedTrue(String email);

	Optional<EmailVerification> findByEmail(String email);
}
