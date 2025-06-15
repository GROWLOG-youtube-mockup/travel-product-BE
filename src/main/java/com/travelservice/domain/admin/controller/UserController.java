package com.travelservice.domain.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.admin.dto.PagedUserResponseDto;
import com.travelservice.domain.admin.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/users")
	public ResponseEntity<PagedUserResponseDto> getAllUsers(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) Integer roleCode) {

		PagedUserResponseDto response = userService.getAllUsers(page, size, roleCode);
		return ResponseEntity.ok(response);
	}
}
