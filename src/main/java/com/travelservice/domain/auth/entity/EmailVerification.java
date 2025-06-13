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
@Table(name = "email_verification")
public class EmailVerification {

	@Id
	@Column(length = 100)
	private String email;

	@Column(length = 6, nullable = false)
	private String code;

	// 0: 미인증(false), 1: 인증됨(true)
	@Column(nullable = false)
	private boolean verified;
}
