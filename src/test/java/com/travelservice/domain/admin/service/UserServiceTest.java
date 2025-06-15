package com.travelservice.domain.admin.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.PagedUserResponseDto;
import com.travelservice.domain.admin.repository.UserRepository;
import com.travelservice.domain.user.entity.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@BeforeEach
	void setUp() {
		userRepository.save(User.builder()
			.name("kim")
			.email("kim1@test.com")
			.password("pw1")
			.phoneNumber("010-1111-1111")
			.roleCode(0)
			.createdAt(LocalDateTime.now().minusMinutes(1))
			.build());

		userRepository.save(User.builder()
			.name("김철수")
			.email("kim@test.com")
			.password("pw2")
			.phoneNumber("010-2222-2222")
			.roleCode(1)
			.createdAt(LocalDateTime.now())
			.build());
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

}