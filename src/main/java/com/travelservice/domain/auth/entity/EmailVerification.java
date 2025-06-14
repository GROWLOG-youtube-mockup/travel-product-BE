package com.travelservice.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	@Setter
	@Column(nullable = false)
	private boolean verified;

	public void updateCode(String code) {
		this.code = code;
	}
}
