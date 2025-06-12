package com.travelservice.domain.order.repository;

import com.travelservice.domain.order.entity.OrderItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
