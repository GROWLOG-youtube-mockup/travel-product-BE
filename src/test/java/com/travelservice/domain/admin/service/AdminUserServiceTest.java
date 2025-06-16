package com.travelservice.domain.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.PagedUserResponseDto;
import com.travelservice.domain.admin.dto.UserUpdateRequestDto;
import com.travelservice.domain.admin.repository.AdminUserRepository;
import com.travelservice.domain.user.entity.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(locations = "classpath:application-local.yml")
class AdminUserServiceTest {

	@Autowired
	private AdminUserRepository userRepository;

	@Autowired
	private AdminUserService userService;

	private Long userId1;
	private Long userId2;

	@BeforeEach
	void setUp() {
		User user1 = userRepository.save(User.builder()
			.name("kim")
			.email("kim1@test.com")
			.password("pw1")
			.phoneNumber("010-1111-1111")
			.roleCode(0)
			.createdAt(LocalDateTime.now().minusMinutes(1))
			.build());

		User user2 = userRepository.save(User.builder()
			.name("김철수")
			.email("kim@test.com")
			.password("pw2")
			.phoneNumber("010-2222-2222")
			.roleCode(1)
			.createdAt(LocalDateTime.now())
			.build());

		userId1 = user1.getUserId();
		userId2 = user2.getUserId();
	}

	@Test
	void 전체_사용자_조회() {
		//given

		//when
		PagedUserResponseDto result = userService.getUsers(1, 10, null);

		//then
		assertEquals(2, result.getTotalElements());
		assertEquals(1, result.getTotalPages());
		assertEquals(1, result.getCurrentPage());
		assertEquals("김철수", result.getContent().get(0).getName()); // 최신순 정렬이므로 나중에 저장한 게 첫번재
		assertEquals("kim", result.getContent().get(1).getName());
	}

	@Test
	void 역할코드로_사용자_조회_테스트() {
		//given

		//when
		PagedUserResponseDto result = userService.getUsers(1, 10, 0);

		//then
		assertEquals(1, result.getTotalElements());
		assertEquals(0, result.getContent().get(0).getRoleCode());
		assertEquals("kim", result.getContent().get(0).getName());
	}

	@Test
	void 빈_결과_조회_테스트() {
		//given

		//when
		PagedUserResponseDto result = userService.getUsers(2, 10, null);

		//then
		assertEquals(0, result.getContent().size());
		assertEquals(2, result.getTotalElements());
		assertEquals(1, result.getTotalPages());
	}

	@Test
	void 유저_업데이트_성공_테스트() {
		// given
		User original = userRepository.findById(userId1).get();

		String newName = "새이름";
		Integer newRoleCode = 2;

		UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
			.name(newName)
			.email(original.getEmail())
			.phoneNumber(original.getPhoneNumber())
			.roleCode(newRoleCode)
			.build();

		// when
		boolean updated = userService.updateUser(userId1, requestDto);

		// then
		assertThat(updated).isTrue();

		User updatedUser = userRepository.findById(userId1).get();
		assertThat(updatedUser.getName()).isEqualTo(newName);
		assertThat(updatedUser.getRoleCode()).isEqualTo(newRoleCode);
	}
}
