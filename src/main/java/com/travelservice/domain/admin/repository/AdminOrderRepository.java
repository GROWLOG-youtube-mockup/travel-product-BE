package com.travelservice.domain.admin.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.travelservice.domain.order.entity.Order;

public interface AdminOrderRepository {
	@Query(value = "SELECT o FROM Order o "
		+ "WHERE (:status IS NULL OR o.status = com.travelservice.enums.OrderStatus.valueOf(:status)) "
		+ "AND (:startDate IS NULL OR o.orderDate >= :startDate) "
		+ "AND (:endDate IS NULL OR o.orderDate <= :endDate) "
		+ "ORDER BY o.orderDate DESC")
	Page<Order> findOrdersByFilter(
		@Param("status") String status,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		Pageable pageable
	);
}
