package com.travelservice.domain.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.travelservice.domain.admin.dto.order.AdminOrderResponseDto;
import com.travelservice.domain.admin.dto.order.PagedAdminOrderResponseDto;
import com.travelservice.domain.admin.repository.AdminOrderRepository;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.enums.OrderStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

	private final AdminOrderRepository adminOrderRepository;

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
				.totalQuantity(order.getTotalQuantity())
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
}
