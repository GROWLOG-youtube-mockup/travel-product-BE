package com.travelservice.domain.admin.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class AdminActionLog {
	@Id
	@GeneratedValue
	private Long logId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	// private User user;

	private int actionType; // Enum도 가능
	private int targetType;
	private Long targetId;

	private LocalDateTime timestamp;
}
