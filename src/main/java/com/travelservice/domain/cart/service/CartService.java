package com.travelservice.domain.cart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.cart.dto.AddToCartRequest;
import com.travelservice.domain.cart.dto.GetCartItemResponse;
import com.travelservice.domain.cart.entity.Cart;
import com.travelservice.domain.cart.repository.CartRepository;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	@Transactional
	public void addToCart(Long userId, AddToCartRequest request) {
		Product product = productRepository.findById(request.getProductId())
			.orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Cart cart = request.toEntity(user, product);
		cartRepository.save(cart);
	}

	@Transactional
	public List<GetCartItemResponse> getCartItems(Long userId) {
		List<Cart> carts = cartRepository.findByUser_UserId(userId);

		if (carts.isEmpty()) {
			throw new CustomException(ErrorCode.CART_EMPTY);
		}

		return carts.stream()
			.map(cart -> GetCartItemResponse.builder()
				.cartItemId(cart.getId())
				.productId(cart.getProduct().getProductId())
				.productName(cart.getProduct().getName())
				.quantity(cart.getQuantity())
				.stockQuantity(cart.getProduct().getStockQuantity())
				.startDate(cart.getStartDate())
				.price(cart.getProduct().getPrice())
				.totalPrice(cart.getProduct().getPrice() * cart.getQuantity())
				.build())
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteCartItem(Long userId, Long cartItemId) {
		Cart cart = cartRepository.findById(cartItemId)
			.orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

		if (!cart.getUser().getUserId().equals(userId)) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
		}

		cartRepository.delete(cart);
	}
}
