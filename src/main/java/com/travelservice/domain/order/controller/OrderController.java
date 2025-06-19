package com.travelservice.domain.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@PostMapping("/from-cart")
	public ResponseEntity<ApiResponse<OrderResponseDto>> orderFromCart(@RequestParam String email) {
		Order order = orderService.createOrderFromCart(email);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.ok(new OrderResponseDto(order)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(@PathVariable Long id) {
		Order order = orderService.findById(id);
		return ResponseEntity.ok(ApiResponse.ok(new OrderResponseDto(order)));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getMyOrders(@RequestParam String email) {
		List<Order> orders = orderService.findOrdersByEmail(email);
		List<OrderResponseDto> dtos = orders.stream()
				.map(OrderResponseDto::new)
				.toList();
		return ResponseEntity.ok(ApiResponse.ok(dtos));
	}
}
