package com.travelservice.domain.user.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	private String name;

	@Column(unique = true)
	private String email;

	private String password;

	@Column(unique = true)
	private String phoneNumber;

	private int roleCode;
}
