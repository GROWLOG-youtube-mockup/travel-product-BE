package com.travelservice.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
