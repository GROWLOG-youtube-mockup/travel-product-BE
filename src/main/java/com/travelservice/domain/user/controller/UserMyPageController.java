package com.travelservice.domain.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.user.dto.DeleteRequestDto;
import com.travelservice.domain.user.dto.NameUpdateRequestDto;
import com.travelservice.domain.user.dto.PasswordUpdateRequestDto;
import com.travelservice.domain.user.dto.PasswordVerifyRequestDto;
import com.travelservice.domain.user.dto.PhoneUpdateRequestDto;
import com.travelservice.domain.user.dto.TripDto;
import com.travelservice.domain.user.dto.UserInfoDto;
import com.travelservice.domain.user.service.UserService;
import com.travelservice.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
@Tag(name = "user API - 회원 마이페이지", description = "회원의 마이페이지 관련 기능을 제공.")
public class UserMyPageController {

	@Autowired
	private UserService userService;

	@Operation(summary = "회원 이름 변경")
	@PatchMapping("/me/name")
	public ApiResponse<String> updateName(@RequestBody NameUpdateRequestDto requestDto, Authentication authentication) {
		userService.updateName(requestDto.getName(), authentication);
		return ApiResponse.ok("이름이 변경되었습니다.");
	}

	@Operation(summary = "전화번호 변경")
	@PatchMapping("/me/phone")
	public ApiResponse<String> updatePhoneNumber(@RequestBody PhoneUpdateRequestDto requestDto,
		Authentication authentication) {
		userService.updatePhoneNumber(requestDto.getPhoneNumber(), authentication);
		return ApiResponse.ok("전화번호가 변경되었습니다.");
	}

	@Operation(summary = "비밀번호 변경")
	@PatchMapping("/me/password")
	public ApiResponse<String> updatePassword(@RequestBody PasswordUpdateRequestDto dto, Authentication auth) {
		userService.updatePassword(dto.getCurrentPassword(), dto.getNewPassword(), auth);
		return ApiResponse.ok("비밀번호가 변경되었습니다.");
	}

	@Operation(summary = "비밀번호 검증")
	@PostMapping("/verify-password")
	public ApiResponse<Map<String, Boolean>> verifyPassword(@RequestBody PasswordVerifyRequestDto requestDto,
		Authentication authentication) {
		boolean verified = userService.verifyPassword(requestDto.getPassword(), authentication);
		return ApiResponse.ok(Map.of("verified", verified));
	}

	@Operation(summary = "회원 탈퇴")
	@DeleteMapping("/me")
	public ApiResponse<String> deleteAccount(@RequestBody DeleteRequestDto requestDto, Authentication auth) {
		userService.deleteAccount(requestDto.getPassword(), auth);
		return ApiResponse.ok("회원 탈퇴가 완료되었습니다.");
	}

	//마이페이지

	@Operation(summary = "내 정보 조회")
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserInfoDto>> getMyInfo(Authentication auth) {
		Long userId = Long.parseLong(auth.getName());
		UserInfoDto userInfo = userService.getMyInfo(userId);
		return ResponseEntity.ok(ApiResponse.ok(userInfo));
	}

	@Operation(summary = "내 여행 내역 조회")
	@GetMapping("/me/trips")
	public ResponseEntity<ApiResponse<List<TripDto>>> getMyTrips(Authentication auth) {
		Long userId = Long.parseLong(auth.getName());
		List<TripDto> trips = userService.getMyTrips(userId);
		return ResponseEntity.ok(ApiResponse.ok(trips));
	}
}
