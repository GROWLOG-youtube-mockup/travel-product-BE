package com.travelservice.domain.admin.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.user.PagedUserResponseDto;
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
			.name("admin")
			.email("admin@test.com")
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
	void testGetAllUsers() {
		//given

		//when
		PagedUserResponseDto result = userService.getUsers(1, 10, null);

		//then
		assertEquals(3, result.getTotalElements());
		assertEquals(1, result.getTotalPages());
		assertEquals(1, result.getCurrentPage());
		assertEquals("superadmin", result.getContent().get(0).getName()); // 최신순 정렬이므로 나중에 저장한 게 첫번재
		assertEquals("admin", result.getContent().get(1).getName());
	}

	@Test
	void shouldReturnEmptyResult_whenRequestingNonExistingPage() {
		//given

		//when
		PagedUserResponseDto result = userService.getUsers(2, 10, null);

		//then
		assertEquals(0, result.getContent().size());
		assertEquals(3, result.getTotalElements());
		assertEquals(1, result.getTotalPages());
	}

	// @Test
	// void shouldUpdateUserSuccessfully() {
	// 	// given
	// 	User original = userRepository.findById(userId1).get();
	//
	// 	String newName = "새이름";
	// 	Integer newRoleCode = 2;
	//
	// 	UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
	// 		.name(newName)
	// 		.email(original.getEmail())
	// 		.phoneNumber(original.getPhoneNumber())
	// 		.roleCode(newRoleCode)
	// 		.build();
	//
	// 	// when
	// 	boolean updated = userService.updateUser(userId1, requestDto);
	//
	// 	// then
	// 	assertThat(updated).isTrue();
	//
	// 	User updatedUser = userRepository.findById(userId1).get();
	// 	assertThat(updatedUser.getName()).isEqualTo(newName);
	// 	assertThat(updatedUser.getRoleCode()).isEqualTo(newRoleCode);
	// }

}
