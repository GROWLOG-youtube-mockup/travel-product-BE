package com.travelservice.domain.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.admin.dto.order.PagedAdminOrderResponseDto;
import com.travelservice.domain.admin.service.AdminOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Tag(name = "주문 관리")
public class AdminOrderController {

	private final AdminOrderService adminOrderService;

	@GetMapping
	@Operation(summary = "주문 목록 조회")
	public ResponseEntity<PagedAdminOrderResponseDto> getOrderList(
		@RequestParam(defaultValue = "1") Integer page,
		@RequestParam(defaultValue = "10") Integer size,
		@RequestParam(required = false) String status,
		@RequestParam(required = false) String start_date,
		@RequestParam(required = false) String end_date
	) {
		PagedAdminOrderResponseDto result = adminOrderService.findOrders(
			page, size, status, start_date, end_date
		);
		return ResponseEntity.ok(result);
	}
}
