package com.travelservice.domain.auth.repository;

import java.util.Optional;

import com.travelservice.domain.auth.entity.PhoneVerification;

public interface PhoneVerificationRepository {
	boolean existsByPhoneNumberAndVerifiedTrue(String phoneNumber);

	Optional<PhoneVerification> findByPhoneNumber(String phoneNumber);
}
