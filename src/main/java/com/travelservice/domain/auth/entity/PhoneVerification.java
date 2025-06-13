package com.travelservice.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "phone_verification")
public class PhoneVerification {
	@Id
	@Column(name = "phone_number", length = 20)
	private String phoneNumber;

	@Column(length = 20, nullable = false)
	private String code;

	// 0: 미인증(false), 1: 인증됨(true)
	@Column(nullable = false)
	private boolean verified;
}
