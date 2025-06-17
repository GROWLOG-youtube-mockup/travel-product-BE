package com.travelservice.domain.cart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.cart.dto.AddToCartRequest;
import com.travelservice.domain.cart.entity.CartItem;
import com.travelservice.domain.cart.repository.CartRepository;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;
	private final ProductRepository productRepository;

	@Transactional
	public void addToCart(User user, AddToCartRequest request) {
		Product product = productRepository.findById(request.getProductId())
			.orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

		CartItem cartItem = request.toEntity(user, product);
		cartRepository.save(cartItem);
	}
}
