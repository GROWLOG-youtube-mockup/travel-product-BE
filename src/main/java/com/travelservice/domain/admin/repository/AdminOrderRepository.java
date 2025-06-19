package com.travelservice.domain.admin.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.travelservice.domain.order.entity.Order;
import com.travelservice.enums.OrderStatus;

@Repository
public interface AdminOrderRepository extends JpaRepository<Order, Long> {
	@Query("SELECT o FROM Order o "
		+ "WHERE (:status IS NULL OR o.status = :status) "
		+ "AND (:startDate IS NULL OR o.orderDate >= :startDate) "
		+ "AND (:endDate IS NULL OR o.orderDate <= :endDate) "
		+ "ORDER BY o.orderDate DESC")
	Page<Order> findOrdersByFilter(
		@Param("status") OrderStatus status,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		Pageable pageable
	);
}
