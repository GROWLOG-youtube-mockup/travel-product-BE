package com.travelservice.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.travelservice.domain.user.dto.UserRegistrationRequestDto;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;

class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	private BCryptPasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		passwordEncoder = new BCryptPasswordEncoder();
	}

	@Test
	void registerMember() {
		// given
		UserRegistrationRequestDto requestDto = UserRegistrationRequestDto.builder()
			.username("userT1")
			.phoneNumber("01012345678")
			.email("test@example.com")
			.password("password1")
			.build();

		User mockUser = User.builder()
			.name(requestDto.getUsername())
			.phoneNumber(requestDto.getPhoneNumber())
			.email(requestDto.getEmail())
			.password(passwordEncoder.encode(requestDto.getPassword()))
			.roleCode(0)
			.createdAt(LocalDateTime.now())
			.build();

		when(userRepository.save(any(User.class))).thenReturn(mockUser);

		// when
		User savedUser = userService.registerMember(requestDto);

		// then
		assertNotNull(savedUser);
		assertEquals(requestDto.getUsername(), savedUser.getName());
		assertEquals(requestDto.getEmail(), savedUser.getEmail());
		assertTrue(passwordEncoder.matches(requestDto.getPassword(), savedUser.getPassword()));
		verify(userRepository, times(1)).save(any(User.class));
	}
}
