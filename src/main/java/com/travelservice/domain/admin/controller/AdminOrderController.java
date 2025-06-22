package com.travelservice.domain.admin.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.admin.dto.order.AdminOrderDetailDto;
import com.travelservice.domain.admin.dto.order.OrderStatusUpdateRequest;
import com.travelservice.domain.admin.dto.order.PagedAdminOrderResponseDto;
import com.travelservice.domain.admin.service.AdminOrderService;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.global.common.ApiResponse;

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
	public ApiResponse<PagedAdminOrderResponseDto> getOrderList(
		@RequestParam(defaultValue = "1") Integer page,
		@RequestParam(defaultValue = "10") Integer size,
		@RequestParam(required = false) String status,
		@RequestParam(required = false) String start_date,
		@RequestParam(required = false) String end_date
	) {
		PagedAdminOrderResponseDto result = adminOrderService.findOrders(
			page, size, status, start_date, end_date
		);
		return ApiResponse.ok(result);
	}

	@GetMapping("/{orderId}")
	@Operation(summary = "주문 상세 조회")
	public ApiResponse<AdminOrderDetailDto> getOrderDetail(@PathVariable Long orderId) {
		AdminOrderDetailDto dto = adminOrderService.getOrderDetail(orderId);
		return ApiResponse.ok(dto);
	}

	@PatchMapping("/{orderId}")
	@Operation(summary = "주문 상태 수정")
	public ApiResponse<?> updateOrderStatus(
		@PathVariable Long orderId,
		@RequestBody OrderStatusUpdateRequest request
	) {
		Order updatedOrder = adminOrderService.updateOrderStatus(orderId, request);

		Map<String, Object> response = Map.of(
			"order_id", updatedOrder.getOrderId(),
			"status", updatedOrder.getStatus(),
			"updated_at", updatedOrder.getUpdatedAt()
		);

		return ApiResponse.ok(response);
	}
}
