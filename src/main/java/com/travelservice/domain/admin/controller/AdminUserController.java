package com.travelservice.domain.admin.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.admin.dto.user.PagedUserResponseDto;
import com.travelservice.domain.admin.dto.user.UserUpdateRequestDto;
import com.travelservice.domain.admin.service.AdminUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
@Tag(name = "사용자 관리")
public class AdminUserController {
	private final AdminUserService userService;

	/**
	 * 사용자 목록 조회
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 * @return 페이징된 사용자 목록
	 */
	@GetMapping("/users")
	@Operation(summary = "사용자 목록 조회")
	public ResponseEntity<PagedUserResponseDto> getUsers(
		@RequestParam(defaultValue = "1") Integer page,
		@RequestParam(defaultValue = "10") Integer size,
		@RequestParam(required = false) Integer roleCode
	) {

		PagedUserResponseDto response = userService.getUsers(page, size, roleCode);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/users/{userId}")
	@Operation(summary = "사용자 정보 수정")
	public ResponseEntity<Map<String, Object>> updateUser(
		@PathVariable Long userId,
		@RequestBody @Valid UserUpdateRequestDto requestDto
	) {
		Map<String, Object> result = userService.updateUser(userId, requestDto);
		return ResponseEntity.ok(result);
	}

	@DeleteMapping("/users/{userId}")
	@Operation(summary = "회원 삭제")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
		boolean deleted = userService.deleteUser(userId);

		if (deleted) {
			return ResponseEntity.ok().body(Map.of("message", "User deleted (soft delete) successfully"));
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
