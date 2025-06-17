package com.travelservice.domain.cart.service;

import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.travelservice.domain.cart.dto.AddToCartRequest;
import com.travelservice.domain.cart.entity.CartItem;
import com.travelservice.domain.cart.repository.CartRepository;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.Region;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private CartRepository cartRepository;
	@Mock
	private ProductRepository productRepository;
	@InjectMocks
	private CartService cartService;

	@Test
	public void addToCart_성공() {
		// given
		User user = User.builder().userId(1L).name("테스트 유저").email("abc@gamil.com")
			.password("123").phoneNumber("000").roleCode(0).build();
		Product product = Product.builder().name("테스트 상품").price(1000)
			.totalQuantity(100).stockQuantity(90).description(" ").type(1)
			.saleStatus(1).duration(3).region(new Region("광주", 1, null)).build();
		product.setProductId(99L);
		productRepository.save(product);

		AddToCartRequest request = new AddToCartRequest(99L, 2, LocalDate.now());

		given(productRepository.findById(99L)).willReturn(Optional.of(product));

		// when
		cartService.addToCart(user, request);
		// then
		then(cartRepository).should().save(any(CartItem.class));
	}

	@Test
	public void addToCart_실패() {
		//gien
		User user = User.builder().userId(1L).name("테스트 유저").email("abc@gamil.com")
			.password("123").phoneNumber("000").roleCode(0).build();
		Product product = Product.builder().name("테스트 상품").price(1000)
			.totalQuantity(100).stockQuantity(90).description(" ").type(1)
			.saleStatus(1).duration(3).region(new Region("광주", 1, null)).build();
		product.setProductId(99L);

		AddToCartRequest request = new AddToCartRequest(99L, 2, LocalDate.now());

		given(productRepository.findById(product.getProductId())).willReturn(Optional.empty());

		// when & then
		Assertions.assertThatThrownBy(() -> cartService.addToCart(user, request))
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
	}

}