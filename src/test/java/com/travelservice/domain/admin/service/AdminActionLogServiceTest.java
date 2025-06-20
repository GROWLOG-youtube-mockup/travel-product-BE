package com.travelservice.domain.admin.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.actionlog.AdminActionLogDto;
import com.travelservice.domain.admin.dto.actionlog.PagedAdminActionLogDto;
import com.travelservice.domain.admin.entity.AdminActionLog;
import com.travelservice.domain.admin.repository.AdminActionLogRepository;
import com.travelservice.domain.admin.repository.AdminUserRepository;
import com.travelservice.domain.user.entity.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminActionLogServiceTest {

	@Autowired
	private AdminActionLogService adminActionLogService;

	@Autowired
	private AdminActionLogRepository adminActionLogRepository;

	@Autowired
	private AdminUserRepository adminUserRepository;

	private User adminUser1;
	private User adminUser2;

	@BeforeEach
	void setUp() {
		adminUser1 = adminUserRepository.save(User.builder()
			.name("관리자A")
			.email("adminA@example.com")
			.password("pw1")
			.phoneNumber("010-1000-1000")
			.roleCode(2) // SUPER_ADMIN
			.createdAt(LocalDateTime.now().minusDays(1))
			.build());
		adminUser2 = adminUserRepository.save(User.builder()
			.name("관리자B")
			.email("adminB@example.com")
			.password("pw2")
			.phoneNumber("010-2000-2000")
			.roleCode(2)
			.createdAt(LocalDateTime.now())
			.build());

		// 로그 3개 생성 (다양한 actionType)
		adminActionLogRepository.save(AdminActionLog.builder()
			.user(adminUser1)
			.actionType(2)
			.targetId(101L)
			.timestamp(LocalDateTime.now().minusHours(2))
			.build());
		adminActionLogRepository.save(AdminActionLog.builder()
			.user(adminUser1)
			.actionType(1)
			.targetId(102L)
			.timestamp(LocalDateTime.now().minusHours(1))
			.build());
		adminActionLogRepository.save(AdminActionLog.builder()
			.user(adminUser2)
			.actionType(2)
			.targetId(103L)
			.timestamp(LocalDateTime.now())
			.build());
	}

	@Test
	void shouldRetrieveAllLogsPaged() {
		// given
		int page = 1;
		int size = 10;
		Long userId = null;
		Integer actionType = null;

		// when
		PagedAdminActionLogDto result = adminActionLogService.getLogs(page, size, userId, actionType);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent().size()).isEqualTo(3);
		assertThat(result.getTotalElements()).isEqualTo(3);
		assertThat(result.getCurrentPage()).isEqualTo(1);
		assertThat(result.getTotalPages()).isEqualTo(1);
	}

	@Test
	void shouldRetrieveLogsByUserId() {
		// given
		int page = 1;
		int size = 10;
		Long userId = adminUser1.getUserId();

		// when
		PagedAdminActionLogDto result = adminActionLogService.getLogs(page, size, userId, null);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent().size()).isEqualTo(2); // adminUser1의 로그만
		for (AdminActionLogDto dto : result.getContent()) {
			assertThat(dto.getUserId()).isEqualTo(userId);
		}
	}

	@Test
	void shouldRetrieveLogsByActionType() {
		// given
		int page = 1;
		int size = 10;
		Integer actionType = 2; // USER_MANAGE 등

		// when
		PagedAdminActionLogDto result = adminActionLogService.getLogs(page, size, null, actionType);

		// then
		assertThat(result).isNotNull();
		// actionType=2 로그만
		assertThat(result.getContent().stream().allMatch(dto -> dto.getActionType() == 2)).isTrue();
		assertThat(result.getContent().size()).isEqualTo(2);
	}

	@Test
	void shouldRetrieveLogsByUserIdAndActionType() {
		// given
		int page = 1;
		int size = 10;
		Long userId = adminUser1.getUserId();
		Integer actionType = 2;

		// when
		PagedAdminActionLogDto result = adminActionLogService.getLogs(page, size, userId, actionType);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent().size()).isEqualTo(1);
		AdminActionLogDto log = result.getContent().get(0);
		assertThat(log.getUserId()).isEqualTo(userId);
		assertThat(log.getActionType()).isEqualTo(2);
	}
}
