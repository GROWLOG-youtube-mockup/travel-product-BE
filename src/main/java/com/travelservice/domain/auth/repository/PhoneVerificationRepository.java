package com.travelservice.domain.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.auth.entity.PhoneVerification;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {
	boolean existsByPhoneNumberAndVerifiedTrue(String phoneNumber);

	Optional<PhoneVerification> findByPhoneNumber(String phoneNumber);
}
