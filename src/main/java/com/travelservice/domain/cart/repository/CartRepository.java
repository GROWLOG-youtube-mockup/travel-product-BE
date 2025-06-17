package com.travelservice.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.cart.entity.CartItem;

public interface CartRepository extends JpaRepository<CartItem, Long> {

}
