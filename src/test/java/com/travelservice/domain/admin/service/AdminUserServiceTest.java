package com.travelservice.domain.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.AdminUserResponseDto;
import com.travelservice.domain.admin.dto.PagedAdminUserResponseDto;
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
	private Long userId3;

	@BeforeEach
	void setUp() {
		User user1 = userRepository.save(User.builder()
			.name("kim")
			.email("kim1@test.com")
			.password("pw1")
			.phoneNumber("010-1111-1111")
			.roleCode(0)
			.createdAt(LocalDateTime.now().minusMinutes(2))
			.build());

		User user2 = userRepository.save(User.builder()
			.name("김철수")
			.email("kim@test.com")
			.password("pw2")
			.phoneNumber("010-2222-2222")
			.roleCode(1)
			.createdAt(LocalDateTime.now().minusMinutes(1))
			.build());

		User user3 = userRepository.save(User.builder()
			.name("superadmin")
			.email("suadmin@test.com")
			.password("pw3")
			.phoneNumber("010-3333-3333")
			.roleCode(2)
			.createdAt(LocalDateTime.now())
			.build());

		userId1 = user1.getUserId();
		userId2 = user2.getUserId();
		userId3 = user3.getUserId();
	}

	@Test
	void 전체_사용자_조회() {
		//given

		//when
		PagedUserResponseDto result = userService.getUsers(1, 10, null);

		//then
		assertEquals(3, result.getTotalElements());
		assertEquals(1, result.getTotalPages());
		assertEquals(1, result.getCurrentPage());
		assertEquals("superadmin", result.getContent().get(0).getName()); // 최신순 정렬이므로 나중에 저장한 게 첫번재
		assertEquals("김철수", result.getContent().get(1).getName());
	}

	@Test
	void 빈_결과_조회_테스트() {
		//given

		//when
		PagedUserResponseDto result = userService.getUsers(2, 10, null);

		//then
		assertEquals(0, result.getContent().size());
		assertEquals(3, result.getTotalElements());
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

	@Test
	void 유저_삭제_성공_테스트() {
		//given
		boolean deleted = userService.deleteUser(userId2);

		//when
		assertThat(deleted).isTrue();

		//then
		User deletedUser = userRepository.findById(userId2).orElse(null);
		assertThat(deletedUser).isNotNull();
		assertThat(deletedUser.getDeletedAt()).isNotNull();
	}

	@Test
	void 관리자_조회_성공_테스트() {
		// given
		int page = 1;
		int size = 10;
		List<Integer> roleCodes = List.of(1, 2);

		// when
		PagedAdminUserResponseDto result = userService.getAdminUsers(page, size);

		// then
		assertThat(result.getContent().size()).isEqualTo(2);
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getTotalPages()).isEqualTo(1);
		assertThat(result.getCurrentPage()).isEqualTo(1);

		List<AdminUserResponseDto> adminUsers = result.getContent();
		assertThat(adminUsers.get(0).getName()).isEqualTo("superadmin");
		assertThat(adminUsers.get(1).getName()).isEqualTo("김철수");
	}
}
