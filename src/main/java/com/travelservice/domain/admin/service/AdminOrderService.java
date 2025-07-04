package com.travelservice.domain.admin.service;

import static com.travelservice.global.common.exception.ErrorCode.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.order.AdminOrderDetailDto;
import com.travelservice.domain.admin.dto.order.AdminOrderResponseDto;
import com.travelservice.domain.admin.dto.order.OrderStatusUpdateRequest;
import com.travelservice.domain.admin.dto.order.PagedAdminOrderResponseDto;
import com.travelservice.domain.admin.entity.AdminActionLog;
import com.travelservice.domain.admin.repository.AdminActionLogRepository;
import com.travelservice.domain.admin.repository.AdminOrderRepository;
import com.travelservice.domain.admin.repository.AdminUserRepository;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.payment.entity.Payment;
import com.travelservice.domain.payment.respository.PaymentRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.enums.OrderStatus;
import com.travelservice.global.common.exception.CustomException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

	private final AdminOrderRepository adminOrderRepository;
	private final PaymentRepository paymentRepository;
	private final AdminUserRepository userRepository;
	private final AdminActionLogRepository adminActionLogRepository;

	public PagedAdminOrderResponseDto findOrders(
		Integer page, Integer size, String status, String startDate, String endDate
	) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderDate").descending());

		LocalDateTime start = (startDate != null && !startDate.isBlank())
			? LocalDate.parse(startDate).atStartOfDay() : null;
		LocalDateTime end = (endDate != null && !endDate.isBlank())
			? LocalDate.parse(endDate).atTime(23, 59, 59) : null;

		// **String → Enum 변환 (유효하지 않으면 null)**
		OrderStatus statusEnum = null;
		if (status != null && !status.isBlank()) {
			try {
				statusEnum = OrderStatus.valueOf(status);
			} catch (IllegalArgumentException e) {
				// 잘못된 값이 들어올 경우 로그 남기고 null 처리
				statusEnum = null;
			}
		}

		Page<Order> orderPage = adminOrderRepository.findOrdersByFilter(statusEnum, start, end, pageable);

		List<AdminOrderResponseDto> content = orderPage.getContent().stream()
			.map(order -> AdminOrderResponseDto.builder()
				.orderId(order.getOrderId())
				.userId(order.getUser().getUserId())
				.userName(order.getUser().getName())
				.userEmail(order.getUser().getEmail())
				.status(order.getStatus().name())
				.peopleCount(order.getTotalQuantity())
				.orderDate(order.getOrderDate())
				.cancelDate(order.getCancelDate())
				.updatedAt(order.getUpdatedAt())
				.build())
			.toList();

		return PagedAdminOrderResponseDto.builder()
			.content(content)
			.totalElements(orderPage.getTotalElements())
			.totalPages(orderPage.getTotalPages())
			.currentPage(page)
			.build();
	}

	@Transactional
	public AdminOrderDetailDto getOrderDetail(Long orderId) {
		Order order = adminOrderRepository.findById(orderId)
			.orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

		Payment payment = paymentRepository.findByOrder_OrderId(orderId).orElse(null);

		return AdminOrderDetailDto.from(order, payment);
	}

	public Order updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
		Order order = adminOrderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));

		order.setStatus(request.getStatus());
		order.setUpdatedAt(LocalDateTime.now());

		// action-log INSERT
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long adminUserId = Long.valueOf(authentication.getName());
		User adminUser = userRepository.findById(adminUserId)
			.orElseThrow(() -> new CustomException(AUTH_INFO_NOT_FOUND));

		AdminActionLog log = AdminActionLog.builder()
			.user(adminUser)
			.actionType(1) // 1: 주문 상태 변경
			.targetId(orderId)
			.timestamp(LocalDateTime.now())
			.build();
		adminActionLogRepository.save(log);

		return adminOrderRepository.save(order);
	}
}
