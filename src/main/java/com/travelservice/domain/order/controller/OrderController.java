package com.travelservice.domain.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.order.dto.OrderRequestDto;
import com.travelservice.domain.order.dto.OrderResponseDto;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.service.OrderService;
import com.travelservice.global.common.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
	private final OrderService orderService;
	@PostMapping
	public ResponseEntity<ApiResponse<OrderResponseDto>> creatOrder(
			@RequestBody OrderRequestDto dto,
			@RequestParam String email
	) {
		Order order = orderService.createOrder(email, dto.getItems());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.ok(new OrderResponseDto(order)));
	}
}
